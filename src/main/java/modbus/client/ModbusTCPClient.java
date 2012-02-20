package modbus.client;

import java.net.InetSocketAddress;
import java.util.BitSet;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import modbus.ModbusConstants;
import modbus.ModbusPipelineFactory;
import modbus.exception.ErrorResponseException;
import modbus.exception.NoResponseException;
import modbus.func.ReadCoilsRequest;
import modbus.func.ReadCoilsResponse;
import modbus.func.WriteSingleCoil;
import modbus.model.ModbusFrame;
import modbus.model.ModbusFunction;
import modbus.model.ModbusHeader;
import modbus.server.ModbusTCPServer;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class ModbusTCPClient {

    static final Logger logger = Logger.getLogger(ModbusTCPClient.class.getSimpleName());
    private final String host;
    private final int port;
    private int lastTransactionIdentifier = 0;
    Channel channel;
    ClientBootstrap bootstrap;

    public ModbusTCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ModbusPipelineFactory(false));

        // Start the connection attempt.
        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection is made successfully.
        channel = connectFuture.awaitUninterruptibly().getChannel();
        if (!connectFuture.isSuccess()) {
            logger.warning(connectFuture.getCause().getLocalizedMessage());
            bootstrap.releaseExternalResources();
        }
    }

    private synchronized int calculateTransactionIdentifier() {
        if (lastTransactionIdentifier < ModbusConstants.TRANSACTION_COUNTER_RESET) {
            lastTransactionIdentifier++;
        } else {
            lastTransactionIdentifier = 1;
        }
        return lastTransactionIdentifier;
    }

    public ModbusFunction callModbusFunction(ModbusFunction function)
            throws NoResponseException, ErrorResponseException {

        int transactionId = calculateTransactionIdentifier();
        int protocolId = 0;
        //length of the Function in byte
        int length = function.calculateLength();
        short unitId = 0;

        ModbusHeader header = new ModbusHeader(transactionId, protocolId, length, unitId);
        ModbusFrame frame = new ModbusFrame(header, function);
        channel.write(frame);

        // Get the handler instance to retrieve the answer.
        ModbusClientHandler handler = (ModbusClientHandler) channel.getPipeline().getLast();

        return handler.getResponse(transactionId).getFunction();
    }

    public WriteSingleCoil writeCoil(int address, boolean state)
            throws NoResponseException, ErrorResponseException {

        WriteSingleCoil wc = new WriteSingleCoil(address, state);

        return (WriteSingleCoil) callModbusFunction(wc);
    }

    public ReadCoilsResponse readCoils(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException {

        ReadCoilsRequest rr = new ReadCoilsRequest(startAddress, quantityOfCoils);

        return (ReadCoilsResponse) callModbusFunction(rr);
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
            host = "localhost";
        } else {
            // Parse options.
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        ModbusTCPServer server = new ModbusTCPServer(port);
        server.run();

        ModbusTCPClient modbusClient = new ModbusTCPClient(host, port);
        modbusClient.run();

        boolean state = true;
        for (int i = 0; i < 20; i++) {
            System.out.println(modbusClient.writeCoil(12321, state));
            Thread.sleep(1000);

            state = state ? false : true;
        }

        modbusClient.close();
    }
}
