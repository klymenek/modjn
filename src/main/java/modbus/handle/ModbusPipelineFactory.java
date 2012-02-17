package modbus.handle;

import modbus.ModbusConstants;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

/**
 *
 * @author ag
 */
public class ModbusPipelineFactory implements
        ChannelPipelineFactory {
    
    private static final ChannelHandler ADU_ENCODER = new ModbusEncoder();
    private static final ChannelHandler ADU_DECODER = new ModbusDecoder();
    
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

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

        //ADU encoder, decoder
        pipeline.addLast("encoder", ADU_ENCODER);
        pipeline.addLast("decoder", ADU_DECODER);

        // and then business logic.
        pipeline.addLast("handler", new ModbusHandler());

        return pipeline;
    }
}
