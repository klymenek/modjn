package de.gandev.modjn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import de.gandev.modjn.entity.func.ModbusError;
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
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.ModbusFunction;
import de.gandev.modjn.entity.ModbusHeader;

/**
 *
 * @author ag
 */
public class ModbusDecoder extends ByteToMessageDecoder {

    private final boolean server;

    public ModbusDecoder(boolean server) {
        this.server = server;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (buffer instanceof EmptyByteBuf) {
            return;
        }

        ModbusHeader mbapHeader = new ModbusHeader(buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedByte());

        short functionCode = buffer.readUnsignedByte();

        ModbusFunction function = null;
        switch (functionCode) {
            case ModbusFunction.READ_COILS:
                if (server) {
                    function = new ReadCoilsRequest();
                } else {
                    function = new ReadCoilsResponse();
                }
                break;
            case ModbusFunction.READ_DISCRETE_INPUTS:
                if (server) {
                    function = new ReadDiscreteInputsRequest();
                } else {
                    function = new ReadDiscreteInputsResponse();
                }
                break;
            case ModbusFunction.READ_INPUT_REGISTERS:
                if (server) {
                    function = new ReadInputRegistersRequest();
                } else {
                    function = new ReadInputRegistersResponse();
                }
                break;
            case ModbusFunction.READ_HOLDING_REGISTERS:
                if (server) {
                    function = new ReadHoldingRegistersRequest();
                } else {
                    function = new ReadHoldingRegistersResponse();
                }
                break;
            case ModbusFunction.WRITE_SINGLE_COIL:
                function = new WriteSingleCoil();
                break;
            case ModbusFunction.WRITE_SINGLE_REGISTER:
                function = new WriteSingleRegister();
                break;
            case ModbusFunction.WRITE_MULTIPLE_COILS:
                if (server) {
                    function = new WriteMultipleCoilsRequest();
                } else {
                    function = new WriteMultipleCoilsResponse();
                }
                break;
            case ModbusFunction.WRITE_MULTIPLE_REGISTERS:
                if (server) {
                    function = new WriteMultipleRegistersRequest();
                } else {
                    function = new WriteMultipleRegistersResponse();
                }
                break;
        }

        if (ModbusFunction.isError(functionCode)) {
            function = new ModbusError(functionCode);
        } else if (function == null) {
            throw new CorruptedFrameException(
                    "Invalid Function Code: " + functionCode);
        }

        function.decode(buffer.readBytes(buffer.readableBytes()));

        ModbusFrame frame = new ModbusFrame(mbapHeader, function);

        out.add(frame);
    }
}
