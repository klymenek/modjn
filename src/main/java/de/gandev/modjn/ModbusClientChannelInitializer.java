package de.gandev.modjn;

import de.gandev.modjn.communication.ModbusClientHandler;
import io.netty.channel.socket.SocketChannel;

/**
 *
 * @author ares
 */
public class ModbusClientChannelInitializer extends ModbusChannelInitializer {

    public ModbusClientChannelInitializer() {
        super();

        MODBUS_DECODER = new ModbusDecoder(false);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        super.initChannel(ch);

        ch.pipeline().addLast("handler", new ModbusClientHandler());
    }
}
