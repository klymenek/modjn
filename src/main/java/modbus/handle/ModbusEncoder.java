package modbus.handle;

import modbus.model.ModbusRequest;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

/**
 *
 * @author ares
 */
public class ModbusEncoder extends SimpleChannelDownstreamHandler {

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ModbusRequest adu = (ModbusRequest) e.getMessage();

        Channels.write(ctx, e.getFuture(), adu.encode());
    }
}
