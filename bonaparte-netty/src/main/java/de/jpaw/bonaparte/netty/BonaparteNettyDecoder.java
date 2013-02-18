package de.jpaw.bonaparte.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class BonaparteNettyDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(BonaparteNettyDecoder.class);
    private final ErrorForwarder errorForwarder;

    public BonaparteNettyDecoder(ErrorForwarder errorForwarder) {
        this.errorForwarder = errorForwarder;
    }

    @Override
    public BonaPortable decode(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] array;
        if (msg.hasArray()) {
            if ((msg.arrayOffset() == 0) && (msg.readableBytes() == msg.capacity())) {
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

        logger.debug("Received {} bytes of data", array.length);
        try {
            ByteArrayParser p = new ByteArrayParser(array, 0, -1);
            BonaPortable obj = p.readRecord();
            logger.debug("Successfully parsed data of class {}", obj.getClass());
            return obj;

        } catch (MessageParserException e) {
            logger.error("Cannot parse {} bytes of data", array.length);
            logger.error("Message received is <{}>", array.length <= 200 ? new String(array) : new String(array).substring(0, 200));
            if (errorForwarder == null) {
                throw e;
            } else {
                return errorForwarder.createErrorObject(e.getErrorCode(), e.getSpecificDescription(), array);
            }
        }
    }
}
