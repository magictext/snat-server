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

public class SReadbytesAndSend extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;

    private int port;

    private int type=Type.date;

    public SReadbytesAndSend(int port,int type){
        this.port = port;
        this.type = type;
        channel=ServerProxyMap.serverProxyMap.get(port);
        Logger.getLogger(this.getClass()).debug(channel);

    }

    public SReadbytesAndSend() {
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ServerChannelMap.serverChannelMap.put(ChannelHashcode.getChannelHashcode(ctx), ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ServerChannelMap.serverChannelMap.remove(ChannelHashcode.getChannelHashcode(ctx));
        ctx.channel().close();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            byte b[] = new byte[msg.readableBytes()];
            msg.readBytes(b);
            Logger.getLogger(this.getClass()).debug("I have received a request");
            channel.writeAndFlush(new Data().setType(Type.date).setSession(ChannelHashcode.getChannelHashcode(ctx)).setPort(port).setB(b));
    }

}
