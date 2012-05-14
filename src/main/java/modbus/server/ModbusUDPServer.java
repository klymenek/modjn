package modbus.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import modbus.ModbusConstants;
import modbus.ModbusPipelineFactory;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

public class ModbusUDPServer {

    private static final Logger logger = Logger.getLogger(ModbusUDPServer.class.getSimpleName());
    private final int port;
    private ConnectionlessBootstrap bootstrap;
    public static final ChannelGroup allChannels = new DefaultChannelGroup("server");

    public ModbusUDPServer(int port) {
        this.port = port;
    }

    public void run() {
        // Configure the client.
        bootstrap = new ConnectionlessBootstrap(new NioDatagramChannelFactory(
                Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ModbusPipelineFactory(true, true));

//        bootstrap.setOption("child.tcpNoDelay", true);
//        bootstrap.setOption("child.keepAlive", true);

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
                    "Usage: " + ModbusUDPServer.class.getSimpleName()
                    + " <port>");
            System.err.println(
                    "Default Usage: <port> = "
                    + ModbusConstants.MODBUS_DEFAULT_PORT);

            port = ModbusConstants.MODBUS_DEFAULT_PORT;
        } else {
            // Parse options.
            port = Integer.parseInt(args[1]);
        }

        ModbusUDPServer modbusServer = new ModbusUDPServer(port);
        modbusServer.run();
    }
}
