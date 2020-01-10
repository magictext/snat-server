package runner;

import handler.ReadMessage;
import handler.SReadbytesAndSend;
import handler.UdpReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import util.Type;

import java.util.HashMap;

public class UdpToClient extends Thread{

    private EventLoopGroup bossGroup;

    private HashMap map;

    private  int port;

    public UdpToClient(int port, EventLoopGroup bossGroup , HashMap map) {
        this.bossGroup = bossGroup;
        this.port = port;
        this.map=map;
    }
    public UdpToClient(int port, EventLoopGroup bossGroup) {
        this.bossGroup = bossGroup;
        this.port = port;
        this.map=map;
    }

    @Override
    public void run() {
        try
        {
            //通过NioDatagramChannel创建Channel，并设置Socket参数支持广播
            //UDP相对于TCP不需要在客户端和服务端建立实际的连接，因此不需要为连接（ChannelPipeline）设置handler
            Bootstrap b=new Bootstrap();
            b.group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() { // (4)
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new DatagramPacketDecoder(new ByteArrayDecoder()))
                                    .addLast(new SReadbytesAndSend(port,Type.udpinfo)); //处理器
                        }
                    });
//            b.bind(port).sync().channel().closeFuture().await();
            Channel future = b.bind(port).sync().channel();
                    map.put(port, future);
                    future.closeFuture().await();
//            ChannelFuture sync = b.bind(port).sync();

//            map.put(port, )
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            bossGroup.shutdownGracefully();
        }


    }
}
