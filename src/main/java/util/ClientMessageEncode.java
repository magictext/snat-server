package util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientMessageEncode extends MessageToByteEncoder<Data> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Data msg, ByteBuf out) throws Exception {

        switch (msg.type){
            case 1: //Type.ConfigSending
                out.writeInt(msg.type);
                out.writeBytes(msg.b);
                break;
            case 200:
                out.writeInt(msg.type)
                .writeInt(msg.port)
                .writeInt(msg.session)
                .writeBytes(msg.getB());
                break;
            case 501:
                out.writeInt(msg.type);
                out.writeBytes(msg.getB());
                break;

            case 4:

                break;


        }
    }
}
