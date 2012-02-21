package modbus;

import modbus.func.ModbusError;
import modbus.func.ReadCoilsRequest;
import modbus.func.ReadCoilsResponse;
import modbus.func.ReadInputRegistersRequest;
import modbus.func.ReadInputRegistersResponse;
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
            case ModbusFunction.READ_INPUT_REGISTERS:
                if (server) {
                    function = new ReadInputRegistersRequest();
                } else {
                    function = new ReadInputRegistersResponse();
                }
                break;
            case ModbusFunction.WRITE_SINGLE_COIL:
                function = new WriteSingleCoil();
                break;
            case ModbusFunction.WRITE_SINGLE_REGISTER:
                function = new WriteSingleRegister();
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
