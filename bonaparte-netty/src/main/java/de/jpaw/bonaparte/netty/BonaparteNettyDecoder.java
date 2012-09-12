package de.jpaw.bonaparte.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class BonaparteNettyDecoder extends MessageToMessageDecoder<ByteBuf, BonaPortable> {
	private static final Logger logger = LoggerFactory.getLogger(BonaparteNettyDecoder.class);
	
    @Override
    public boolean isDecodable(Object msg) throws Exception {
        return msg instanceof ByteBuf;
    }

    @Override
    public BonaPortable decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] array;
        if (msg.hasArray()) {
            if (msg.arrayOffset() == 0 && msg.readableBytes() == msg.capacity()) {
                // we have no offset and the length is the same as the capacity. Its safe to reuse
                // the array without copy it first
                array = msg.array();
            } else {
                // copy the ChannelBuffer to a byte array
                array = new byte[msg.readableBytes()];
                msg.getBytes(0, array);
            }
        } else {
            // copy the ChannelBuffer to a byte array
            array = new byte[msg.readableBytes()];
            msg.getBytes(0, array);
        }
        
        ByteArrayParser p = new ByteArrayParser(array, 0, -1);
        BonaPortable obj = p.readRecord();
        logger.trace("Receiving data of class {}", obj.getClass());
        return obj;
    }
}
