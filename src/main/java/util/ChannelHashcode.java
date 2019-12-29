package util;

import io.netty.channel.ChannelHandlerContext;

public class ChannelHashcode {
    public static int getChannelHashcode(ChannelHandlerContext ctx){
        return ctx.channel().id().hashCode();
    }
}
