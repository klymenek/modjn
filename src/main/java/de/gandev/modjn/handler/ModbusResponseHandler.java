package de.gandev.modjn.handler;

import de.gandev.modjn.ModbusConstants;
import de.gandev.modjn.entity.ModbusFrame;
import de.gandev.modjn.entity.exception.ErrorResponseException;
import de.gandev.modjn.entity.exception.NoResponseException;
import de.gandev.modjn.entity.func.ModbusError;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ares
 */
public abstract class ModbusResponseHandler extends SimpleChannelInboundHandler<ModbusFrame> {

    private static final Logger logger = Logger.getLogger(ModbusResponseHandler.class.getSimpleName());
    private final Map<Integer, ModbusFrame> responses = new HashMap<>(ModbusConstants.TRANSACTION_IDENTIFIER_MAX);

    public ModbusFrame getResponse(int transactionIdentifier)
            throws NoResponseException, ErrorResponseException {

        long timeoutTime = System.currentTimeMillis() + ModbusConstants.SYNC_RESPONSE_TIMEOUT;
        ModbusFrame frame;
        do {
            frame = responses.get(transactionIdentifier);
        } while (frame == null && (timeoutTime - System.currentTimeMillis()) > 0);

        if (frame != null) {
            responses.remove(transactionIdentifier);
        }

        if (frame == null) {
            throw new NoResponseException();
        } else if (frame.getFunction() instanceof ModbusError) {
            throw new ErrorResponseException((ModbusError) frame.getFunction());
        }

        return frame;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.SEVERE, cause.getLocalizedMessage());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusFrame response) throws Exception {
        responses.put(response.getHeader().getTransactionIdentifier(), response);
        newResponse(response);

    }

    public abstract void newResponse(ModbusFrame frame);
}
