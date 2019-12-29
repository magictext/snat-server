package util;

import config.ClientConfig;
import config.ConfigEntity;
import exception.RangePortException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.MessageToMessageDecoder;
import map.ServerProxyMap;
import org.apache.commons.collections.BidiMap;
import org.apache.log4j.Logger;
import runner.RemoteServerToClient;

import java.util.ArrayList;
import java.util.List;


public class ClientMassageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logger.getLogger(this.getClass()).warn(cause.getMessage(), cause);
    }

    private void configureServerForNewClient(ClientConfig config, ChannelHandlerContext ctx) throws RangePortException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        List<Integer> list = new ArrayList<>();
        for (ConfigEntity entity : config.getList()) {
            switch (entity.getName()){
                case "":
                    break;
            }
            //获得端口映射表
            BidiMap port = ((BidiMap)RangePort.getRangePort(entity.getPort(), entity.getRemotePort())).inverseBidiMap();
            //加入总表中
            for (Object o : port.keySet()) {
                int serverport= (Integer)o;
                if (ServerProxyMap.serverProxyMap.containsKey(serverport)==true) {
                    list.add(serverport);
                } else {
                    ServerProxyMap.serverProxyMap.put(serverport,ctx.channel());
                    //开启serverSocket
                    new Thread(new RemoteServerToClient((Integer) o,bossGroup, workerGroup),this.getClass().getName()+o).start();
                }
            }
            if(list.size()!=0){
                ctx.channel().writeAndFlush(new Data().setType(Type.portBindsError).setB(Mapper.getJsonByte(list)));
            }
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int lenght=in.readableBytes();
        Logger.getLogger(this.getClass()).debug("the brfore length is: "+lenght);
        int i = in.readInt();
        byte b[];
        switch (i){
            case 1:
                b=new byte[lenght - 4];
                Logger.getLogger(this.getClass()).debug("the after length is : "+in.readableBytes());
                in.readBytes(b);
                ClientConfig config = Mapper.parseObject(b, ClientConfig.class);
                Logger.getLogger(this.getClass()).debug("received config:   "+config);
                configureServerForNewClient(config,ctx);
                break;
            case 501:
                b=new byte[lenght - 4];
                in.readBytes(b);
                List list = Mapper.parseObject(b, List.class);
                Logger.getLogger(this.getClass()).warn("server port: "+list.toString()+" is already in use");
                Logger.getLogger(this.getClass()).warn("the port above has not been running");
                break;
            case 200:
                Data data=new Data();
                data.setType(200).setPort(in.readInt()).setSession(in.readInt());
                b=new byte[in.readableBytes()];
                in.readBytes(b);
                data.setB(b);
                out.add(data);
                break;
        }
    }

}
