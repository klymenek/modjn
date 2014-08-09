package de.gandev.modjn.entity.func;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import de.gandev.modjn.entity.ModbusFunction;

/**
 *
 * @author ares
 */
public class WriteSingleRegister extends ModbusFunction {

    // raw vars
    private int registerAddress;
    private int registerValue;

    /*
     * Constructor for Response
     */
    public WriteSingleRegister() {
        super(WRITE_SINGLE_REGISTER);
    }

    /*
     * Constructor for Request
     */
    public WriteSingleRegister(int outputAddress, int value) {
        super(WRITE_SINGLE_REGISTER);

        this.registerAddress = outputAddress;
        this.registerValue = value;
    }

    public int getRegisterAddress() {
        return registerAddress;
    }

    public int getRegisterValue() {
        return registerValue;
    }

    @Override
    public int calculateLength() {
        //Function Code + Output Address + Output Value, in Byte + 1 for Unit Identifier
        return 1 + 2 + 2 + 1;
    }

    public void decode(ByteBuf data) {
        registerAddress = data.readUnsignedShort();
        registerValue = data.readUnsignedShort();
    }

    public ByteBuf encode() {
        ByteBuf buf = Unpooled.buffer(calculateLength());
        buf.writeByte(getFunctionCode());
        buf.writeShort(registerAddress);
        buf.writeShort(registerValue);

        return buf;
    }

    @Override
    public String toString() {
        return "WriteSingleInputRegister{" + "registerAddress=" + registerAddress + ", registerValue=" + registerValue + '}';
    }
}
