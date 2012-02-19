package modbus.server;

import java.util.logging.Logger;
import modbus.func.WriteCoil;
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
            ModbusFrame request = (ModbusFrame) message;
            ModbusFunction func = request.getFunction();
            
            if(func instanceof WriteCoil) {
                WriteCoil function = (WriteCoil) func;
                logger.info(function.toString());
                
                e.getChannel().write(message);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.warning(e.getCause().getLocalizedMessage());
        e.getChannel().close();
    }
}
