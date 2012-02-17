package modbus.model;

/**
 *
 * @author ares
 */
public abstract class ModbusFunction {
    
    public static final int WRITE_COIL = 0x05;

    private final int functionCode;

    public ModbusFunction(int functionCode) {
        this.functionCode = functionCode;
    }

    public int getFunctionCode() {
        return functionCode;
    }
}
