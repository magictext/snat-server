package handler;
import	java.util.HashMap;

import config.ClientConfig;
import config.ConfigEntity;
import exception.RangePortException;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import runner.TcpToClient;
import runner.UdpToClient;
import util.Data;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import map.ServerChannelMap;
import map.ServerProxyMap;
import util.Mapper;
import util.RangePort;
import util.Type;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


//此处理器用于服务器接收客户端代理的信息并进行转发
public class ReadMessage extends SimpleChannelInboundHandler<Data> {

    private static EventLoopGroup bossGroup = new NioEventLoopGroup();
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();
    //此客户端对应的连接信息
    HashMap<Integer,Channel> port = new HashMap<>();
    //发生错误的端口号列表
    List<Integer> list = new ArrayList<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Logger.getLogger(this.getClass()).debug("received an connection");
        //服务端接收用户代理连接并记录下对应端口和channel
//        Channel channel = ctx.channel();
//        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.localAddress();
//        int port=inetSocketAddress.getPort();
//        ServerProxyMap.serverProxyMap.put(port, ctx.channel());
    }

    private void configureTCP(ConfigEntity entity, ChannelHandlerContext ctx) throws RangePortException {
        BidiMap bidiMap = ((BidiMap) RangePort.getRangePort(entity.getLocalServer(), entity.getPort(), entity.getRemotePort())).inverseBidiMap();
        //加入总表中
        for (Object o : bidiMap.keySet()) {
            int serverport= (Integer)o;
            if (ServerProxyMap.serverProxyMap.containsKey(serverport)==true) {
                list.add(serverport);
            } else {
                ServerProxyMap.serverProxyMap.put(serverport,ctx.channel());
                //开启serverSocket
                new Thread(new TcpToClient((Integer) o,bossGroup, workerGroup,port),this.getClass().getName()+o).start();
            }
        }
    }
    private void configureUDP(ConfigEntity entity, ChannelHandlerContext ctx) throws RangePortException {
        BidiMap bidiMap = ((BidiMap) RangePort.getRangePort(entity.getLocalServer(), entity.getPort(), entity.getRemotePort())).inverseBidiMap();
        port.putAll(bidiMap);
        //加入总表中
        for (Object o : bidiMap.keySet()) {
            int serverport= (Integer)o;
            if (ServerProxyMap.serverProxyMap.containsKey(serverport)==true) {
                list.add(serverport);
            } else {
                ServerProxyMap.serverProxyMap.put(serverport,ctx.channel());
                //开启serverSocket
                new Thread(new UdpToClient((Integer) o, workerGroup,port),this.getClass().getName()+o).start();
            }
        }
    }
    private void configureSUDP(ConfigEntity entity, ChannelHandlerContext ctx) {
    }

    private void configureServerForNewClient(ClientConfig config, ChannelHandlerContext ctx) throws RangePortException {

        for (ConfigEntity entity : config.getList()) {
            switch (entity.getName()){
                case "tcp":
                    configureTCP(entity, ctx);
                    break;
                case "udp":
                    configureUDP(entity, ctx);
                    break;
                case "sudp":
                    configureSUDP(entity, ctx);
                    break;
            }
            if(list.size()!=0){
                ctx.channel().writeAndFlush(new Data().setType(Type.portBindsError).setB(Mapper.getJsonByte(list)));
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data msg) throws Exception {
        Logger.getLogger(this.getClass()).debug(msg);
        switch (msg.type){
            case 1:
                ClientConfig config = Mapper.parseObject(msg.getB(), ClientConfig.class);
                Logger.getLogger(this.getClass()).debug("received config:   "+config);
                configureServerForNewClient(config,ctx);
                break;
            case 200: case 201:
                Channel channel = ServerChannelMap.serverChannelMap.get(msg.session);
                //由于此channel没有绑定编码器 理论上会直接输出。
                channel.writeAndFlush(msg.getB());
                break;
        }
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        for (Integer key : port.keySet()) {
            Channel channel = port.get(key);
            ServerProxyMap.serverProxyMap.remove(key);
            Logger.getLogger(this.getClass()).debug(ServerProxyMap.serverProxyMap.toString());
            channel.close();
            Logger.getLogger(this.getClass()).debug(key+" has free");
        }

            port.clear();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException){
            for (Integer key : port.keySet()) {
                Channel channel = port.get(key);
                ServerProxyMap.serverProxyMap.remove(key);
                Logger.getLogger(this.getClass()).debug(ServerProxyMap.serverProxyMap.toString());
                channel.close();
                Logger.getLogger(this.getClass()).debug(key+" has free");
            }
            port.clear();
            return;
        }
        Logger.getLogger(this.getClass()).warn(cause.getMessage(), cause);
    }
}
