package modbus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import modbus.func.WriteCoil;
import modbus.handle.ModbusHandler;
import modbus.handle.ModbusPipelineFactory;
import modbus.model.ModbusHeader;
import modbus.model.ModbusRequest;
import modbus.model.ModbusResponse;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class ModbusTCPClient {

    private final String host;
    private final int port;
    Channel channel;
    ClientBootstrap bootstrap;

    public ModbusTCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ModbusPipelineFactory());

        // Start the connection attempt.
        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection is made successfully.
        channel = connectFuture.awaitUninterruptibly().getChannel();
        if (!connectFuture.isSuccess()) {
            System.out.println(connectFuture.getCause().getLocalizedMessage());
            bootstrap.releaseExternalResources();
        }
    }

    public ModbusResponse writeCoil(int address, boolean state) {
        int value = 0x0000;
        if(state) value = 0xFF00;
        
        ModbusHeader header = new ModbusHeader(1, 0, 6, (short) 0);
        WriteCoil wc = new WriteCoil(address, value);
        ModbusRequest adu = new ModbusRequest(header, wc);
        channel.write(adu);

        // Get the handler instance to retrieve the answer.
        ModbusHandler handler = (ModbusHandler) channel.getPipeline().getLast();

        return handler.getResponse();
    }
    
    public void close() {
        channel.close();
        channel.getCloseFuture().awaitUninterruptibly();
        
        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
    }

    public static void main(String[] args) throws Exception {
        String host;
        int port;

        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + ModbusTCPClient.class.getSimpleName()
                    + " <host> <port>");
            System.err.println(
                    "Default Usage: <host> = localhost <port> = "
                    + ModbusConstants.MODBUS_DEFAULT_PORT);

            port = ModbusConstants.MODBUS_DEFAULT_PORT;
            host = "192.168.1.55";
        } else {
            // Parse options.
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        
        ModbusTCPClient modbusClient = new ModbusTCPClient(host, port);
        modbusClient.run();
        
        System.out.println(modbusClient.writeCoil(12321, true));
        Thread.sleep(1000);
        System.out.println(modbusClient.writeCoil(12321, false));
        Thread.sleep(1000);
        System.out.println(modbusClient.writeCoil(12321, true));
        Thread.sleep(1000);
        System.out.println(modbusClient.writeCoil(12321, false));
        
        modbusClient.close();
    }
}
