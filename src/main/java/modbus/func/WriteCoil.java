package modbus.func;

import modbus.model.ModbusFunction;
import modbus.model.RequestInterface;
import modbus.model.ResponseInterface;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author ares
 */
public class WriteCoil extends ModbusFunction implements RequestInterface, ResponseInterface {

    private int outputAddress;
    private int outputValue;

    /*
     * Constructor for Response
     */
    public WriteCoil() {
        super(WRITE_COIL);
    }

    /*
     * Constructor for Request
     */
    public WriteCoil(int outputAddress, int outputValue) {
        super(WRITE_COIL);

        this.outputAddress = outputAddress;
        this.outputValue = outputValue;
    }

    public int getOutputAddress() {
        return outputAddress;
    }

    public int getOutputValue() {
        return outputValue;
    }

    public void decode(ChannelBuffer data) {
        outputAddress = data.readUnsignedShort();
        outputValue = data.readUnsignedShort();
    }

    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.buffer(5);
        buf.writeByte(getFunctionCode());
        buf.writeShort(outputAddress);
        buf.writeShort(outputValue);

        return buf;
    }

    @Override
    public String toString() {
        return "WriteCoilRequest{" + "outputAddress=" + outputAddress + ", outputValue=" + outputValue + '}';
    }
}
