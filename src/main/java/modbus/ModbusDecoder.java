package modbus;

import modbus.func.ModbusError;
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
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 *
 * @author ag
 */
public class ModbusDecoder extends FrameDecoder {

    private final boolean server;

    public ModbusDecoder(boolean server) {
        this.server = server;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel,
            ChannelBuffer buffer) throws Exception {

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

        return frame;
    }
}
