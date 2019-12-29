package runner;

import handler.SReadbytesAndSend;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.apache.log4j.Logger;

public class RemoteServerToClient implements Runnable{

    private int port;


    public RemoteServerToClient(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.port = port;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    EventLoopGroup bossGroup ;

    EventLoopGroup workerGroup ;

    public void run() {

        try {
            Logger.getLogger(this.getClass()).debug("Thread: "+Thread.currentThread().getName()+"  port:  "+port);
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                             .addLast(new ByteArrayEncoder())
                             .addLast(new SReadbytesAndSend()); //处理器
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
	}

}
