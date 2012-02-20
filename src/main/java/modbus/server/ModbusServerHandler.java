package modbus.server;

import java.util.BitSet;
import java.util.logging.Logger;
import modbus.func.ReadCoilsRequest;
import modbus.func.ReadCoilsResponse;
import modbus.func.WriteSingleCoil;
import modbus.model.ModbusFrame;
import modbus.model.ModbusFunction;
import modbus.model.ModbusHeader;
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
public class ModbusServerHandler extends SimpleChannelUpstreamHandler {

    static final Logger logger = Logger.getLogger(ModbusServerHandler.class.getSimpleName());

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ModbusTCPServer.allChannels.add(e.getChannel());
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
            ModbusFrame frame = (ModbusFrame) message;
            ModbusFunction function = frame.getFunction();

            if (function instanceof WriteSingleCoil) {
                WriteSingleCoil request = (WriteSingleCoil) function;
                logger.info(request.toString());

                e.getChannel().write(message);
            } else if (function instanceof ReadCoilsRequest) {
                ReadCoilsRequest request = (ReadCoilsRequest) function;
                logger.info(request.toString());

                BitSet coils = new BitSet(request.getQuantityOfCoils());
                coils.set(0);
                coils.set(5);
                coils.set(8);

                ReadCoilsResponse response = new ReadCoilsResponse(coils);
                ModbusHeader header = new ModbusHeader(
                        frame.getHeader().getTransactionIdentifier(),
                        frame.getHeader().getProtocolIdentifier(),
                        response.calculateLength(),
                        frame.getHeader().getUnitIdentifier());

                ModbusFrame responseFrame = new ModbusFrame(header, response);

                e.getChannel().write(responseFrame);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warning(e.getCause().getLocalizedMessage());
        e.getChannel().close();
    }
}
