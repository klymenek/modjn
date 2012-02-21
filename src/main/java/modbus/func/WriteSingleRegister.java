package modbus.func;

import modbus.model.ModbusFunction;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

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

    public void decode(ChannelBuffer data) {
        registerAddress = data.readUnsignedShort();
        registerValue = data.readUnsignedShort();
    }

    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.buffer(calculateLength());
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
