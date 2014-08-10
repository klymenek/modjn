package de.gandev.modjn;

import de.gandev.modjn.entity.exception.ConnectionException;
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModbusServer {

    public static enum CONNECTION_STATES {

        listening, down, clientsConnected
    }

    public static final String PROP_CONNECTIONSTATE = "connectionState";
    //
    private final int port;
    private ServerBootstrap bootstrap;
    private CONNECTION_STATES connectionState = CONNECTION_STATES.down;
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private Channel parentChannel;
    private final ChannelGroup clientChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ModbusServer(int port) {
        this.port = port;
    }

    public void setup(ModbusRequestHandler handler) throws ConnectionException {
        handler.setServer(this);

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
            parentChannel = bootstrap.bind(port).sync().channel();

            setConnectionState(CONNECTION_STATES.listening);

            parentChannel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();

                    setConnectionState(CONNECTION_STATES.down);
                }
            });
        } catch (Exception ex) {
            setConnectionState(CONNECTION_STATES.down);
            Logger.getLogger(ModbusServer.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());

            throw new ConnectionException(ex.getLocalizedMessage());
        }
    }

    public CONNECTION_STATES getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(CONNECTION_STATES connectionState) {
        CONNECTION_STATES oldConnectionState = this.connectionState;
        this.connectionState = connectionState;
        propertyChangeSupport.firePropertyChange(PROP_CONNECTIONSTATE, oldConnectionState, connectionState);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void close() {
        if (parentChannel != null) {
            parentChannel.close().awaitUninterruptibly();
        }
        clientChannels.close().awaitUninterruptibly();
    }

    public ChannelGroup getClientChannels() {
        return clientChannels;
    }

    public void addClient(Channel channel) {
        clientChannels.add(channel);
        setConnectionState(CONNECTION_STATES.clientsConnected);
    }

    public void removeClient(Channel channel) {
        clientChannels.remove(channel);
        setConnectionState(CONNECTION_STATES.clientsConnected);
    }
}
