package modbus.handle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import modbus.model.ModbusResponse;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 *
 * @author ares
 */
public class ModbusHandler extends SimpleChannelUpstreamHandler {

    static final Logger logger = Logger.getLogger(ModbusHandler.class.getSimpleName());
    final BlockingQueue<ModbusResponse> answer = new LinkedBlockingQueue<ModbusResponse>();

    public ModbusResponse getResponse() {
        try {
            ModbusResponse adu = answer.take();
            return adu;
        } catch (InterruptedException ex) {
            logger.warning(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public void handleUpstream(
            ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        boolean offered = answer.offer((ModbusResponse) e.getMessage());
        assert offered;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warning(e.getCause().getLocalizedMessage());
        e.getChannel().close();
    }
}
