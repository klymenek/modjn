package de.gandev.modjn;

import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.ModbusFunction;
import de.gandev.modjn.entity.ModbusHeader;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.ReadCoilsRequest;
import de.gandev.modjn.entity.func.ReadCoilsResponse;
import de.gandev.modjn.entity.func.ReadDiscreteInputsRequest;
import de.gandev.modjn.entity.func.ReadDiscreteInputsResponse;
import de.gandev.modjn.entity.func.ReadHoldingRegistersRequest;
import de.gandev.modjn.entity.func.ReadHoldingRegistersResponse;
import de.gandev.modjn.entity.func.ReadInputRegistersRequest;
import de.gandev.modjn.entity.func.ReadInputRegistersResponse;
import de.gandev.modjn.entity.func.WriteMultipleCoilsRequest;
import de.gandev.modjn.entity.func.WriteMultipleCoilsResponse;
import de.gandev.modjn.entity.func.WriteMultipleRegistersRequest;
import de.gandev.modjn.entity.func.WriteMultipleRegistersResponse;
import de.gandev.modjn.entity.func.WriteSingleCoil;
import de.gandev.modjn.entity.func.WriteSingleRegister;
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
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModbusClient {

    private static final Logger logger = Logger.getLogger(ModbusClient.class.getSimpleName());
    private final String host;
    private final int port;
    private int lastTransactionIdentifier = 0;
    private Channel channel;
    private Bootstrap bootstrap;
    private short unitId;

    public ModbusClient(String host, int port) {
        this(host, port, (short) 255);
    }

    public ModbusClient(String host, int port, short unitId) {
        this.host = host;
        this.port = port;
        this.unitId = unitId;
    }

    public void setup() {
        try {
            final EventLoopGroup workerGroup = new NioEventLoopGroup();

            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ModbusChannelInitializer(null));

            ChannelFuture f = bootstrap.connect(host, port).sync();

            channel = f.channel();

            channel.closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    workerGroup.shutdownGracefully();
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(ModbusClient.class.getName()).log(Level.SEVERE, null, ex);
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

        if (channel == null) {
            return null;
        }

        int transactionId = calculateTransactionIdentifier();
        int protocolId = 0;
        //length of the Function in byte
        int length = function.calculateLength();

        ModbusHeader header = new ModbusHeader(transactionId, protocolId, length, unitId);
        ModbusFrame frame = new ModbusFrame(header, function);
        channel.writeAndFlush(frame);

        ModbusResponseHandler handler = (ModbusResponseHandler) channel.pipeline().get("responseHandler");
        if (handler == null) {
            return null;
        }

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

    public Channel getChannel() {
        return channel;
    }

    public void close() {
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
    }
}
