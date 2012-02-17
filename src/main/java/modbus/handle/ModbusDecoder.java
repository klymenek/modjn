package modbus.handle;

import modbus.func.WriteCoil;
import modbus.model.ModbusFunction;
import modbus.model.ModbusHeader;
import modbus.model.ModbusResponse;
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

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        ModbusHeader mbapHeader = new ModbusHeader(buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedByte());

        ModbusResponse modbusADU = new ModbusResponse(mbapHeader);

        short functionCode = buffer.readUnsignedByte();

        switch (functionCode) {
            case ModbusFunction.WRITE_COIL:
                WriteCoil response = new WriteCoil();
                response.decode(buffer.readBytes(buffer.readableBytes()));

                modbusADU.setResponse(response);
                break;
            default:
                //buffer.resetReaderIndex();
                throw new CorruptedFrameException(
                        "Invalid Function Code: " + functionCode);
        }

        return modbusADU;
    }
}
