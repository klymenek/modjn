package modbus.model;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author ag
 */
public class ModbusFrame {

    private final ModbusHeader header;
    private ModbusFunction function;

    public ModbusFrame(ModbusHeader header, ModbusFunction function) {
        this.header = header;
        this.function = function;
    }

    public ModbusHeader getHeader() {
        return header;
    }

    public ModbusFunction getFunction() {
        return function;
    }

    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeShort(header.getTransactionIdentifier());
        buf.writeShort(header.getProtocolIdentifier());
        buf.writeShort(header.getLength());
        buf.writeByte(header.getUnitIdentifier());

        buf.writeBytes(function.encode());

        return buf;
    }

    @Override
    public String toString() {
        return "ModbusClientRequest{" + "header=" + header + ", function=" + function + '}';
    }
}
