package modbus.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import modbus.ModbusConstants;
import modbus.exception.ErrorResponseException;
import modbus.exception.NoResponseException;
import modbus.model.ModbusFrame;
import modbus.model.ModbusFunction;
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
public class ModbusClientHandler extends SimpleChannelUpstreamHandler {

    static final Logger logger = Logger.getLogger(ModbusClientHandler.class.getSimpleName());
    final Map<Integer, ModbusFrame> responses = new HashMap<Integer, ModbusFrame>(ModbusConstants.TRANSACTION_COUNTER_RESET);

    public ModbusFrame getResponse(int transactionIdentifier)
            throws NoResponseException, ErrorResponseException {

        long timeoutTime = System.currentTimeMillis() + ModbusConstants.RESPONSE_TIMEOUT;
        ModbusFrame frame;
        do {
            frame = responses.get(transactionIdentifier);
        } while (frame == null && (timeoutTime - System.currentTimeMillis()) > 0);
        
        if(frame != null) responses.remove(transactionIdentifier);

        if (frame == null) {
            throw new NoResponseException();
        } else if (ModbusFunction.isError(frame.getFunction().getFunctionCode())) {
            throw new ErrorResponseException(frame.getFunction().getFunctionCode());
        }

        return frame;
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
        Object message = e.getMessage();

        if (message instanceof ModbusFrame) {
            ModbusFrame response = (ModbusFrame) message;
            responses.put(response.getHeader().getTransactionIdentifier(), response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warning(e.getCause().getLocalizedMessage());
        e.getChannel().close();
    }
}
