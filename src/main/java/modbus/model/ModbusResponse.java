package modbus.model;

/**
 *
 * @author ag
 */
public class ModbusResponse {

    private final ModbusHeader header;
    private ResponseInterface response;

    public ModbusResponse(ModbusHeader header) {
        this.header = header;
    }

    public ModbusHeader getHeader() {
        return header;
    }

    public ResponseInterface getResponse() {
        return response;
    }

    public void setResponse(ResponseInterface response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ModbusResponseADU{" + "header=" + header + ", response=" + response + '}';
    }
}
