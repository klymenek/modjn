package de.gandev.modjn;

import de.gandev.modjn.handler.ModbusChannelInitializer;
import de.gandev.modjn.handler.ModbusRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
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

    public void setup() {
        try {
            final EventLoopGroup bossGroup = new NioEventLoopGroup();
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ModbusChannelInitializer(handler))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            Channel parentChannel = bootstrap.bind(port).sync().channel();

            allChannels.add(parentChannel);

            parentChannel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(ModbusServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        allChannels.close().awaitUninterruptibly();
    }
}
