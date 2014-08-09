package de.gandev.modjn.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer();

        buf.writeShort(header.getTransactionIdentifier());
        buf.writeShort(header.getProtocolIdentifier());
        buf.writeShort(header.getLength());
        buf.writeByte(header.getUnitIdentifier());

        buf.writeBytes(function.encode());

        return buf;
    }

    @Override
    public String toString() {
        return "ModbusFrame{" + "header=" + header + ", function=" + function + '}';
    }
}
