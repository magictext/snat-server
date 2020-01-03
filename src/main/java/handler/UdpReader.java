package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import map.ServerChannelMap;
import map.ServerProxyMap;
import util.ChannelHashcode;
import util.Data;
import util.Type;

import java.net.InetSocketAddress;

public class UdpReader extends SimpleChannelInboundHandler<DatagramPacket> {

    private Channel channel;
    private int port;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel= ServerProxyMap.serverProxyMap.get(((InetSocketAddress) ctx.channel().localAddress()).getPort());
        port=((InetSocketAddress) ctx.channel().localAddress()).getPort();
        ServerChannelMap.serverChannelMap.put(ChannelHashcode.getChannelHashcode(ctx), ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf content = msg.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        channel.writeAndFlush(new Data().setType(Type.udpinfo).setSession(ChannelHashcode.getChannelHashcode(ctx)).setPort(port).setB(bytes));
    }
}
