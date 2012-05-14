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
import modbus.func.ReadDiscreteInputsRequest;
import modbus.func.ReadDiscreteInputsResponse;
import modbus.func.ReadHoldingRegistersRequest;
import modbus.func.ReadHoldingRegistersResponse;
import modbus.func.ReadInputRegistersRequest;
import modbus.func.ReadInputRegistersResponse;
import modbus.func.WriteMultipleCoilsRequest;
import modbus.func.WriteMultipleCoilsResponse;
import modbus.func.WriteMultipleRegistersRequest;
import modbus.func.WriteMultipleRegistersResponse;
import modbus.func.WriteSingleCoil;
import modbus.func.WriteSingleRegister;
import modbus.model.ModbusFrame;
import modbus.model.ModbusFunction;
import modbus.model.ModbusHeader;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;

public class ModbusUDPClient {

    private static final Logger logger = Logger.getLogger(ModbusUDPClient.class.getSimpleName());
    private final String host;
    private final int port;
    private int lastTransactionIdentifier = 0;
    private Channel channel;
    private ConnectionlessBootstrap bootstrap;

    public ModbusUDPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        // Configure the client.
        bootstrap = new ConnectionlessBootstrap(new NioDatagramChannelFactory(
                Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ModbusPipelineFactory(false, true));

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

        WriteSingleCoil wsc = new WriteSingleCoil(address, state);

        return (WriteSingleCoil) callModbusFunction(wsc);
    }

    public WriteSingleRegister writeRegister(int address, int value)
            throws NoResponseException, ErrorResponseException {

        WriteSingleRegister wsr = new WriteSingleRegister(address, value);

        return (WriteSingleRegister) callModbusFunction(wsr);
    }

    public ReadCoilsResponse readCoils(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException {

        ReadCoilsRequest rcr = new ReadCoilsRequest(startAddress, quantityOfCoils);

        return (ReadCoilsResponse) callModbusFunction(rcr);
    }

    public ReadDiscreteInputsResponse readDiscreteInputs(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException {

        ReadDiscreteInputsRequest rdir = new ReadDiscreteInputsRequest(startAddress, quantityOfCoils);

        return (ReadDiscreteInputsResponse) callModbusFunction(rdir);
    }

    public ReadInputRegistersResponse readInputRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException {

        ReadInputRegistersRequest rirr = new ReadInputRegistersRequest(startAddress, quantityOfInputRegisters);

        return (ReadInputRegistersResponse) callModbusFunction(rirr);
    }

    public ReadHoldingRegistersResponse readHoldingRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException {

        ReadHoldingRegistersRequest rhrr = new ReadHoldingRegistersRequest(startAddress, quantityOfInputRegisters);

        return (ReadHoldingRegistersResponse) callModbusFunction(rhrr);
    }

    public WriteMultipleCoilsResponse writeMultipleCoils(int address, int quantityOfOutputs, BitSet outputsValue)
            throws NoResponseException, ErrorResponseException {

        WriteMultipleCoilsRequest wmcr = new WriteMultipleCoilsRequest(address, quantityOfOutputs, outputsValue);

        return (WriteMultipleCoilsResponse) callModbusFunction(wmcr);
    }

    public WriteMultipleRegistersResponse writeMultipleRegisters(int address, int quantityOfRegisters, int[] registers)
            throws NoResponseException, ErrorResponseException {

        WriteMultipleRegistersRequest wmrr = new WriteMultipleRegistersRequest(address, quantityOfRegisters, registers);

        return (WriteMultipleRegistersResponse) callModbusFunction(wmrr);
    }

    public void close() {
        channel.close();
        channel.getCloseFuture().awaitUninterruptibly();

        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
    }
//
//    public static void main(String[] args) throws Exception {
//        String host;
//        int port;
//
//        // Print usage if no argument is specified.
//        if (args.length != 2) {
//            System.err.println(
//                    "Usage: " + ModbusTCPClient.class.getSimpleName()
//                    + " <host> <port>");
//            System.err.println(
//                    "Default Usage: <host> = localhost <port> = "
//                    + ModbusConstants.MODBUS_DEFAULT_PORT);
//
//            port = ModbusConstants.MODBUS_DEFAULT_PORT;
//            host = "192.168.1.55";
//        } else {
//            // Parse options.
//            host = args[0];
//            port = Integer.parseInt(args[1]);
//        }
//
//        ModbusTCPServer server = new ModbusTCPServer(port);
//        server.run();
//
//        ModbusTCPClient client = new ModbusTCPClient(host, port);
//        client.run();
//
//        boolean state = true;
//        for (int i = 0; i < 20; i++) {
//            System.out.println(client.writeCoil(12321, state));
//            //Thread.sleep(1000);
//
//            state = state ? false : true;
//        }
//
//        ReadCoilsResponse readCoils = client.readCoils(12321, 5);
//        System.out.println(readCoils);
//
//        ReadInputRegistersResponse readInputRegisters = client.readInputRegisters(12321, 6);
//        System.out.println(readInputRegisters);
//
//        client.close();
//
//        server.close();
//    }
}
