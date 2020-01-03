package handler;

import config.ClientConfig;
import config.ConfigEntity;
import io.netty.channel.Channel;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import runner.UdpToClient;
import util.Data;
import util.Mapper;
import util.RangePort;
import util.Type;
import exception.RangePortException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.MessageToMessageDecoder;
import map.ServerProxyMap;
import org.apache.commons.collections.BidiMap;
import org.apache.log4j.Logger;
import runner.TcpToClient;

import java.util.ArrayList;
import java.util.List;


public class ClientMassageDecoder extends MessageToMessageDecoder<ByteBuf> {




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
                out.add(new Data().setType(Type.configSending).setB(b));
                break;
            case 501:
                b=new byte[lenght - 4];
                in.readBytes(b);
                List list = Mapper.parseObject(b, List.class);
                Logger.getLogger(this.getClass()).warn("server port: "+list.toString()+" is already in use");
                Logger.getLogger(this.getClass()).warn("the port above has not been running");
                break;
            case 200: case 201:
                Data data=new Data();
                data.setType(i).setPort(in.readInt()).setSession(in.readInt());
                b=new byte[in.readableBytes()];
                in.readBytes(b);
                data.setB(b);
                out.add(data);
                break;}
    }

}
