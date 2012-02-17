package modbus.model;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 *
 * @author ag
 */
public class ModbusRequest {

    private final ModbusHeader header;
    private RequestInterface request;

    public ModbusRequest(ModbusHeader header, RequestInterface request) {
        this.header = header;
        this.request = request;
    }

    public ModbusHeader getHeader() {
        return header;
    }

    public RequestInterface getRequest() {
        return request;
    }

    public ChannelBuffer encode() {
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeShort(header.getTransactionIdentifier());
        buf.writeShort(header.getProtocolIdentifier());
        buf.writeShort(header.getLength());
        buf.writeByte(header.getUnitIdentifier());

        buf.writeBytes(request.encode());

        return buf;
    }

    @Override
    public String toString() {
        return "ModbusRequestADU{" + "header=" + header + ", request=" + request + '}';
    }
}
