package de.gandev.modjn;

import de.gandev.modjn.handler.ModbusChannelInitializer;
import de.gandev.modjn.handler.ModbusRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModbusServer {

    private static final Logger logger = Logger.getLogger(ModbusServer.class.getSimpleName());
    private final int port;
    private ServerBootstrap bootstrap;
    public static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final ModbusRequestHandler handler;

    public ModbusServer(int port, ModbusRequestHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void run() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ModbusChannelInitializer(handler))
                            .option(ChannelOption.SO_BACKLOG, 128)
                            .childOption(ChannelOption.SO_KEEPALIVE, true);

                    // Bind and start to accept incoming connections.
                    ChannelFuture f = bootstrap.bind(port).sync();

                    allChannels.add(f.channel());

                    // Wait until the server socket is closed.
                    // In this example, this does not happen, but you can do that to gracefully
                    // shut down your server.
                    f.channel().closeFuture().sync();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ModbusServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            }
        };

        Thread serverThread = new Thread(r);
        serverThread.start();
    }

    public void close() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();
    }
}
