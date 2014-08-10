package de.gandev.modjn;

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
    private short unitId;
    private CONNECTION_STATES connectionState = CONNECTION_STATES.notConnected;

    public ModbusClient(String host, int port) {
        this(host, port, (short) 255);
    }

    public ModbusClient(String host, int port, short unitId) {
        this.host = host;
        this.port = port;
        this.unitId = unitId;
    }

    public void setup() throws ConnectionException {
        try {
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ModbusChannelInitializer(null));

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

    private synchronized int calculateTransactionIdentifier() {
        if (lastTransactionIdentifier < ModbusConstants.TRANSACTION_COUNTER_RESET) {
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

    public ModbusFunction callModbusFunction(ModbusFunction function)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        if (channel == null) {
            throw new ConnectionException("Not connected!");
        }

        int transactionId = calculateTransactionIdentifier();
        int protocolId = 0;
        int pduLength = function.calculateLength();

        ModbusHeader header = new ModbusHeader(transactionId, protocolId, pduLength, unitId);
        ModbusFrame frame = new ModbusFrame(header, function);
        channel.writeAndFlush(frame);

        ModbusResponseHandler handler = (ModbusResponseHandler) channel.pipeline().get("responseHandler");
        if (handler == null) {
            throw new ConnectionException("Not connected!");
        }

        return handler.getResponse(transactionId).getFunction();
    }

    public WriteSingleCoil writeSingleCoil(int address, boolean state)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        WriteSingleCoil wsc = new WriteSingleCoil(address, state);

        return (WriteSingleCoil) callModbusFunction(wsc);
    }

    public WriteSingleRegister writeSingleRegister(int address, int value)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        WriteSingleRegister wsr = new WriteSingleRegister(address, value);

        return (WriteSingleRegister) callModbusFunction(wsr);
    }

    public ReadCoilsResponse readCoils(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        ReadCoilsRequest rcr = new ReadCoilsRequest(startAddress, quantityOfCoils);

        return (ReadCoilsResponse) callModbusFunction(rcr);
    }

    public ReadDiscreteInputsResponse readDiscreteInputs(int startAddress, int quantityOfCoils)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        ReadDiscreteInputsRequest rdir = new ReadDiscreteInputsRequest(startAddress, quantityOfCoils);

        return (ReadDiscreteInputsResponse) callModbusFunction(rdir);
    }

    public ReadInputRegistersResponse readInputRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        ReadInputRegistersRequest rirr = new ReadInputRegistersRequest(startAddress, quantityOfInputRegisters);

        return (ReadInputRegistersResponse) callModbusFunction(rirr);
    }

    public ReadHoldingRegistersResponse readHoldingRegisters(int startAddress, int quantityOfInputRegisters)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        ReadHoldingRegistersRequest rhrr = new ReadHoldingRegistersRequest(startAddress, quantityOfInputRegisters);

        return (ReadHoldingRegistersResponse) callModbusFunction(rhrr);
    }

    public WriteMultipleCoilsResponse writeMultipleCoils(int address, int quantityOfOutputs, BitSet outputsValue)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        WriteMultipleCoilsRequest wmcr = new WriteMultipleCoilsRequest(address, quantityOfOutputs, outputsValue);

        return (WriteMultipleCoilsResponse) callModbusFunction(wmcr);
    }

    public WriteMultipleRegistersResponse writeMultipleRegisters(int address, int quantityOfRegisters, int[] registers)
            throws NoResponseException, ErrorResponseException, ConnectionException {

        WriteMultipleRegistersRequest wmrr = new WriteMultipleRegistersRequest(address, quantityOfRegisters, registers);

        return (WriteMultipleRegistersResponse) callModbusFunction(wmrr);
    }

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }
}
