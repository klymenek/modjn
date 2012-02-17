package modbus;

/**
 *
 * @author ag
 */
public class ModbusConstants {
    public static final int MODBUS_DEFAULT_PORT = 502;
    
    public static final int MBAP_LENGTH = 7;
    public static final int FUNCTION_CODE_LENGTH = 1;   
    public static final int ADU_MAX_LENGTH = 260;   
    
    public static final int DATA_MAX_LENGTH = ADU_MAX_LENGTH - MBAP_LENGTH - FUNCTION_CODE_LENGTH;    
}
