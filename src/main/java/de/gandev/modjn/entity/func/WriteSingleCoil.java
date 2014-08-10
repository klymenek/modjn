package de.gandev.modjn.entity.func;

import io.netty.buffer.ByteBuf;

/**
 *
 * @author ares
 */
public class WriteSingleCoil extends AbstractFunction {

    //outputAddress;
    //outputValue;
    private boolean state;

    public WriteSingleCoil() {
        super(WRITE_SINGLE_COIL);
    }

    public WriteSingleCoil(int outputAddress, boolean state) {
        super(WRITE_SINGLE_COIL, outputAddress, state ? 0xFF00 : 0x0000);

        this.state = state;
    }

    public int getOutputAddress() {
        return address;
    }

    @Override
    public void decode(ByteBuf data) {
        super.decode(data);

        state = value == 0xFF00;
    }

    public boolean isState() {
        return state;
    }

    @Override
    public String toString() {
        return "WriteSingleCoil{" + "outputAddress=" + address + ", state=" + state + '}';
    }
}
