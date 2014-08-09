package de.gandev.modjn.handler;

import de.gandev.modjn.ModbusConstants;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author ag
 */
public class ModbusChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandler MODBUS_ENCODER;
    protected ChannelHandler MODBUS_DECODER;
    //
    private final ModbusRequestHandler handler;

    public ModbusChannelInitializer(ModbusRequestHandler handler) {
        MODBUS_ENCODER = new ModbusEncoder();

        this.handler = handler;

        MODBUS_DECODER = new ModbusDecoder(handler != null);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        /*
         * Modbus TCP Frame Description
         *  - max. 260 Byte (ADU = 7 Byte MBAP + 253 Byte PDU)
         *  - Length field includes Unit Identifier + PDU
         *
         * <----------------------------------------------- ADU -------------------------------------------------------->
         * <---------------------------- MBAP -----------------------------------------><------------- PDU ------------->
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         * | Transaction Identifier | Protocol Identifier | Length   | Unit Identifier || Function Code | Data          |
         * | (2 Byte)               | (2 Byte)            | (2 Byte) | (1 Byte)        || (1 Byte)      | (1 - 252 Byte |
         * +------------------------+---------------------+----------+-----------------++---------------+---------------+
         */
        pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(ModbusConstants.ADU_MAX_LENGTH, 4, 2));

        //Modbus encoder, decoder
        pipeline.addLast("encoder", MODBUS_ENCODER);
        pipeline.addLast("decoder", MODBUS_DECODER);

        if (handler != null) {
            pipeline.addLast("requestHandler", handler);
        } else {
            pipeline.addLast("responseHandler", new ModbusResponseHandler());
        }
    }
}
