package de.jpaw.bonaparte.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class BonaparteNettyDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(BonaparteNettyDecoder.class);
    private final ErrorForwarder errorForwarder;

    public BonaparteNettyDecoder(ErrorForwarder errorForwarder) {
        this.errorForwarder = errorForwarder;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, MessageBuf<Object> out) throws Exception {
        byte[] array;
        if (msg.hasArray()) {
            if ((msg.arrayOffset() == 0) && (msg.readableBytes() == msg.capacity())) {
                // we have no offset and the length is the same as the capacity. Its safe to reuse
                // the array without copy it first
                array = msg.array();
            } else {
                // copy the ChannelBuffer to a byte array
                array = new byte[msg.readableBytes()];
                msg.readBytes(array);
            }
        } else {
            // copy the ChannelBuffer to a byte array
            array = new byte[msg.readableBytes()];
            msg.readBytes(array);
        }

        logger.debug("Received {} bytes of data", array.length);
        try {
            ByteArrayParser p = new ByteArrayParser(array, 0, -1);
            BonaPortable obj = p.readRecord();
            logger.debug("Successfully parsed data of class {}", obj.getClass());
            out.add(obj);
        } catch (MessageParserException e) {
            logger.error("Cannot parse {} bytes of data", array.length);
            logger.error("Message received is <{}>", array.length <= 200 ? new String(array) : new String(array).substring(0, 200));
            if (errorForwarder == null) {
                throw e;
            } else {
                out.add(errorForwarder.createErrorObject(e.getErrorCode(), e.getSpecificDescription(), array));
            }
        }
    }
}
