package runner;

import handler.SReadbytesAndSend;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import map.ServerChannelMap;
import map.ServerProxyMap;
import org.apache.commons.collections.BidiMap;
import org.apache.log4j.Logger;
import util.Type;

import java.util.HashMap;


public class TcpToClient implements Runnable{

    private int port;

    private HashMap<Integer, Channel> bidiMap;

    public TcpToClient(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup, HashMap bidiMap) {
        this.port = port;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.bidiMap=bidiMap;
    }

    EventLoopGroup bossGroup ;

    EventLoopGroup workerGroup ;

    public void run() {

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                             .addLast(new ByteArrayEncoder())
                             .addLast(new SReadbytesAndSend(port, Type.date)); //处理器
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            bidiMap.put(port,f.channel());
            Logger.getLogger(this.getClass()).debug("Thread: "+Thread.currentThread().getName()+"  port:  "+port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
