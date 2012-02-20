package modbus;

/**
 *
 * @author ag
 */
public class ModbusConstants {
    public static final int MODBUS_DEFAULT_PORT = 502;
    
    public static final int ERROR_OFFSET = 0x80;
    
    public static final int RESPONSE_TIMEOUT = 2000; //milliseconds
    public static final int TRANSACTION_COUNTER_RESET = 16;
    
    public static final int ADU_MAX_LENGTH = 260;   
}
