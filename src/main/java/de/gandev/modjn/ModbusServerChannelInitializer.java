package de.gandev.modjn;

import de.gandev.modjn.communication.ModbusServerHandler;
import io.netty.channel.socket.SocketChannel;

/**
 *
 * @author ares
 */
public class ModbusServerChannelInitializer extends ModbusChannelInitializer {

    private ModbusServerHandler handler;

    public ModbusServerChannelInitializer(ModbusServerHandler handler) {
        super();

        MODBUS_DECODER = new ModbusDecoder(true);

        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);

        ch.pipeline().addLast("handler", handler);
    }
}
