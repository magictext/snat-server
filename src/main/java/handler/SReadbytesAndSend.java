package handler;

import util.ChannelHashcode;
import util.Data;
import util.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import map.ServerChannelMap;
import map.ServerProxyMap;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class SReadbytesAndSend extends SimpleChannelInboundHandler {

    private Channel channel;

    private int port;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel=ServerProxyMap.serverProxyMap.get(((InetSocketAddress) ctx.channel().localAddress()).getPort());
        port=((InetSocketAddress) ctx.channel().localAddress()).getPort();
        ServerChannelMap.serverChannelMap.put(ChannelHashcode.getChannelHashcode(ctx), ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerChannelMap.serverChannelMap.remove(ChannelHashcode.getChannelHashcode(ctx));
        ctx.close().sync();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf mes = null;
        if (msg instanceof ByteBuf) {
            mes = (ByteBuf) msg;
            byte b[] = new byte[mes.readableBytes()];
            mes.readBytes(b);
            Logger.getLogger(this.getClass()).debug(new String(b));
            channel.writeAndFlush(new Data().setType(Type.date).setSession(ChannelHashcode.getChannelHashcode(ctx)).setPort(port).setB(b));
        }
    }

}
