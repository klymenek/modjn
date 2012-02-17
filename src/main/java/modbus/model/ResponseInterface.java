package modbus.model;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 * @author ares
 */
public interface ResponseInterface {
    public void decode(ChannelBuffer data);
}
