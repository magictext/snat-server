package handler;

import util.ChannelHashcode;
import util.Data;
import util.Type;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//服务器接收来自外部的连接 所用的handler 发送数据 port 和 session
//客户端代理接收数据 发送给服务器
public class ReadbytesAndSend extends SimpleChannelInboundHandler {


    //如果这个channel为空 则是服务端使用
    private Channel channel;

    private int port;

    public ReadbytesAndSend(Channel channel) {
        this.channel = channel;
    }

    public ReadbytesAndSend(Channel channel, int port) {
        this.channel = channel;
        this.port = port;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ReadbytesAndSend() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf mes = null;
        if (msg instanceof ByteBuf) {
            mes = (ByteBuf) msg;
            byte b[] = new byte[mes.readableBytes()];
            mes.readBytes(b);
            channel.writeAndFlush(new Data().setType(Type.date).setSession(ChannelHashcode.getChannelHashcode(ctx)).setPort(port).setB(b));
        }
    }
}
