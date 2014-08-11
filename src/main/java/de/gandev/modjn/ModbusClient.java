package de.gandev.modjn;

import static de.gandev.modjn.ModbusConstants.DEFAULT_PROTOCOL_IDENTIFIER;
import static de.gandev.modjn.ModbusConstants.DEFAULT_UNIT_IDENTIFIER;
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.ModbusFunction;
import de.gandev.modjn.entity.ModbusHeader;
import de.gandev.modjn.entity.exception.ConnectionException;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.WriteSingleCoil;
import de.gandev.modjn.entity.func.WriteSingleRegister;
import de.gandev.modjn.entity.func.request.ReadCoilsRequest;
import de.gandev.modjn.entity.func.request.ReadDiscreteInputsRequest;
import de.gandev.modjn.entity.func.request.ReadHoldingRegistersRequest;
import de.gandev.modjn.entity.func.request.ReadInputRegistersRequest;
import de.gandev.modjn.entity.func.request.WriteMultipleCoilsRequest;
import de.gandev.modjn.entity.func.request.WriteMultipleRegistersRequest;
import de.gandev.modjn.entity.func.response.ReadCoilsResponse;
import de.gandev.modjn.entity.func.response.ReadDiscreteInputsResponse;
import de.gandev.modjn.entity.func.response.ReadHoldingRegistersResponse;
import de.gandev.modjn.entity.func.response.ReadInputRegistersResponse;
import de.gandev.modjn.entity.func.response.WriteMultipleCoilsResponse;
import de.gandev.modjn.entity.func.response.WriteMultipleRegistersResponse;
import de.gandev.modjn.handler.ModbusChannelInitializer;
import de.gandev.modjn.handler.ModbusResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModbusClient {

    public static enum CONNECTION_STATES {

        connected, notConnected, pending
    }

    public static final String PROP_CONNECTIONSTATE = "connectionState";
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    //
    private final String host;
    private final int port;
    private int lastTransactionIdentifier = 0;
    private Channel channel;
    private Bootstrap bootstrap;
    private short unitIdentifier;
    private short protocolIdentifier;
    private CONNECTION_STATES connectionState = CONNECTION_STATES.notConnected;

    public ModbusClient(String host, int port) {
        this(host, port, DEFAULT_UNIT_IDENTIFIER, DEFAULT_PROTOCOL_IDENTIFIER);
    }

    public ModbusClient(String host, int port, short unitIdentifier) {
        this(host, port, unitIdentifier, DEFAULT_PROTOCOL_IDENTIFIER);
    }

    public ModbusClient(String host, int port, short unitId, short protocolIdentifier) {
        this.host = host;
        this.port = port;
        this.unitIdentifier = unitId;
        this.protocolIdentifier = protocolIdentifier;
    }

    public void setup() throws ConnectionException {
        setup(null);
    }

    public void setup(ModbusResponseHandler handler) throws ConnectionException {
        try {
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ModbusChannelInitializer(handler));

            setConnectionState(CONNECTION_STATES.pending);

            ChannelFuture f = bootstrap.connect(host, port).sync();

            setConnectionState(CONNECTION_STATES.connected);

            channel = f.channel();

            channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();

                    setConnectionState(CONNECTION_STATES.notConnected);
                }
            });
        } catch (Exception ex) {
            setConnectionState(CONNECTION_STATES.notConnected);
            Logger.getLogger(ModbusClient.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());

            throw new ConnectionException(ex.getLocalizedMessage());
        }

    }

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }

    private synchronized int calculateTransactionIdentifier() {
        if (lastTransactionIdentifier < ModbusConstants.TRANSACTION_IDENTIFIER_MAX) {
            lastTransactionIdentifier++;
        } else {
            lastTransactionIdentifier = 1;
        }
        return lastTransactionIdentifier;
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

    public int callModbusFunction(ModbusFunction function)
            throws ConnectionException {

        if (channel == null) {
            throw new ConnectionException("Not connected!");
        }

        int transactionId = calculateTransactionIdentifier();
        int pduLength = function.calculateLength();

        ModbusHeader header = new ModbusHeader(transactionId, protocolIdentifier, pduLength, unitIdentifier);
        ModbusFrame frame = new ModbusFrame(header, function);
        channel.writeAndFlush(frame);

        return transactionId;
    }

    public <V extends ModbusFunction> V callModbusFunctionSync(ModbusFunction function)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        int transactionId = callModbusFunction(function);

        ModbusResponseHandler handler = (ModbusResponseHandler) channel.pipeline().get("responseHandler");
        if (handler == null) {
            throw new ConnectionException("Not connected!");
        }

        //TODO handle cast exception!?
        return (V) handler.getResponse(transactionId).getFunction();
    }

    //async function execution
    public int writeSingleCoilAsync(int address, boolean state) throws ConnectionException {
        return callModbusFunction(new WriteSingleCoil(address, state));
    }

    public int writeSingleRegisterAsync(int address, int value) throws ConnectionException {
        return callModbusFunction(new WriteSingleRegister(address, value));
    }

    public int readCoilsAsync(int startAddress, int quantityOfCoils) throws ConnectionException {
        return callModbusFunction(new ReadCoilsRequest(startAddress, quantityOfCoils));
    }

    public int readDiscreteInputsAsync(int startAddress, int quantityOfCoils) throws ConnectionException {
        return callModbusFunction(new ReadDiscreteInputsRequest(startAddress, quantityOfCoils));
    }

    public int readInputRegistersAsync(int startAddress, int quantityOfInputRegisters) throws ConnectionException {
        return callModbusFunction(new ReadInputRegistersRequest(startAddress, quantityOfInputRegisters));
    }

    public int readHoldingRegistersAsync(int startAddress, int quantityOfInputRegisters) throws ConnectionException {
        return callModbusFunction(new ReadHoldingRegistersRequest(startAddress, quantityOfInputRegisters));
    }

    public int writeMultipleCoilsAsync(int address, int quantityOfOutputs, BitSet outputsValue) throws ConnectionException {
        return callModbusFunction(new WriteMultipleCoilsRequest(address, quantityOfOutputs, outputsValue));
    }

    public int writeMultipleRegistersAsync(int address, int quantityOfRegisters, int[] registers) throws ConnectionException {
        return callModbusFunction(new WriteMultipleRegistersRequest(address, quantityOfRegisters, registers));
    }

    //sync function execution
    public WriteSingleCoil writeSingleCoil(int address, boolean state)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<WriteSingleCoil>callModbusFunctionSync(new WriteSingleCoil(address, state));
    }

    public WriteSingleRegister writeSingleRegister(int address, int value)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<WriteSingleRegister>callModbusFunctionSync(new WriteSingleRegister(address, value));
    }

    public ReadCoilsResponse readCoils(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<ReadCoilsResponse>callModbusFunctionSync(new ReadCoilsRequest(startAddress, quantityOfCoils));
    }

    public ReadDiscreteInputsResponse readDiscreteInputs(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<ReadDiscreteInputsResponse>callModbusFunctionSync(new ReadDiscreteInputsRequest(startAddress, quantityOfCoils));
    }

    public ReadInputRegistersResponse readInputRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<ReadInputRegistersResponse>callModbusFunctionSync(new ReadInputRegistersRequest(startAddress, quantityOfInputRegisters));
    }

    public ReadHoldingRegistersResponse readHoldingRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<ReadHoldingRegistersResponse>callModbusFunctionSync(new ReadHoldingRegistersRequest(startAddress, quantityOfInputRegisters));
    }

    public WriteMultipleCoilsResponse writeMultipleCoils(int address, int quantityOfOutputs, BitSet outputsValue)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<WriteMultipleCoilsResponse>callModbusFunctionSync(new WriteMultipleCoilsRequest(address, quantityOfOutputs, outputsValue));
    }

    public WriteMultipleRegistersResponse writeMultipleRegisters(int address, int quantityOfRegisters, int[] registers)
            throws NoResponseException, ErrorResponseException, ConnectionException {
        return this.<WriteMultipleRegistersResponse>callModbusFunctionSync(new WriteMultipleRegistersRequest(address, quantityOfRegisters, registers));
    }
}
