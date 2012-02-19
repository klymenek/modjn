package modbus;

import modbus.func.WriteCoil;
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

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        ModbusHeader mbapHeader = new ModbusHeader(buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                buffer.readUnsignedByte());

        short functionCode = buffer.readUnsignedByte();

        ModbusFunction function = null;
        switch (functionCode) {
            case ModbusFunction.WRITE_COIL:
                function = new WriteCoil();
                function.decode(buffer.readBytes(buffer.readableBytes()));
                break;
            default:
                //buffer.resetReaderIndex();
                throw new CorruptedFrameException(
                        "Invalid Function Code: " + functionCode);
        }
        
                ModbusFrame modbusADU = new ModbusFrame(mbapHeader, function);

        return modbusADU;
    }
}
