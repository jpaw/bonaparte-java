package de.jpaw.bonaparte.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
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

    public static byte [] getData(ByteBuf msg) {
        byte[] array;
        if (msg.readableBytes() <= 0 || msg.getByte(msg.readableBytes()-1) != '\n') {
            logger.debug("Ignoring {} bytes of data - no NL at end of message", msg.readableBytes());
            return null;  // message not yet complete
        }
        
        if (msg.hasArray()) {
            if ((msg.arrayOffset() == 0) && (msg.readableBytes() == msg.capacity())) {
                // we have no offset and the length is the same as the capacity. Its safe to reuse
                // the array without copy it first
                array = msg.array();
                msg.skipBytes(msg.readableBytes());  // void copying, as would be done via read()
                return array;
            }
        }
        // copy the ChannelBuffer to a byte array
        array = new byte[msg.readableBytes()];
        msg.readBytes(array);
        return array;
    }
    
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] array = getData(msg);
        if (array == null)
            return;  // no data yet...
        
        logger.debug("Received {} bytes of data", array.length);
        try {
            ByteArrayParser p = new ByteArrayParser(array, 0, -1);
            BonaPortable obj = p.readRecord();
            logger.debug("Successfully parsed data of class {}", obj.getClass());
            out.add(obj);
        } catch (MessageParserException e) {
            // http://co-de-generation.blogspot.de/2012/09/slf4j-doesnt-log-exception-stacktrace.html
            logger.error("Cannot parse " + array.length + " bytes of data: Exception {}", e);
            logger.error("Message received is <{}>", array.length <= 200 ? new String(array) : new String(array).substring(0, 200));
            if (errorForwarder == null) {
                throw e;
            } else {
                out.add(errorForwarder.createErrorObject(e.getErrorCode(), e.getMessage(), array));
            }
        }
    }
}
