package modbus.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import modbus.ModbusConstants;
import modbus.ModbusPipelineFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class ModbusTCPServer {

    static final Logger logger = Logger.getLogger(ModbusTCPServer.class.getSimpleName());
    private final int port;
    ServerBootstrap bootstrap;
    static final ChannelGroup allChannels = new DefaultChannelGroup("server");

    public ModbusTCPServer(int port) {
        this.port = port;
    }

    public void run() {
        // Configure the client.
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ModbusPipelineFactory(true));

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        Channel parentChannel = bootstrap.bind(new InetSocketAddress(port));

        allChannels.add(parentChannel);
    }

    public void close() {
        ChannelGroupFuture future = allChannels.close();
        future.awaitUninterruptibly();
        
        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
    }

    public static void main(String[] args) throws Exception {
        int port;

        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + ModbusTCPServer.class.getSimpleName()
                    + " <port>");
            System.err.println(
                    "Default Usage: <port> = "
                    + ModbusConstants.MODBUS_DEFAULT_PORT);

            port = ModbusConstants.MODBUS_DEFAULT_PORT;
        } else {
            // Parse options.
            port = Integer.parseInt(args[1]);
        }

        ModbusTCPServer modbusServer = new ModbusTCPServer(port);
        modbusServer.run();
    }
}
