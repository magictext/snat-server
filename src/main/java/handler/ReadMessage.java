package handler;

import util.Data;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import map.ServerChannelMap;
import map.ServerProxyMap;

import java.net.InetSocketAddress;


//此处理器用于服务器接收客户端代理的信息并进行转发
public class ReadMessage extends SimpleChannelInboundHandler<Data> {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //服务端接收用户代理连接并记录下对应端口和channel
        Channel channel = ctx.channel();
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.localAddress();
        int port=inetSocketAddress.getPort();
        ServerProxyMap.serverProxyMap.put(port, ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerProxyMap.serverProxyMap.remove(((InetSocketAddress) ctx.channel().localAddress()).getPort());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data msg) throws Exception {
        switch (msg.type){
            case 200:
                Channel channel = ServerChannelMap.serverChannelMap.get(msg.session);
                //由于此channel没有绑定编码器 理论上会直接输出。
                channel.writeAndFlush(msg.getB());
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
