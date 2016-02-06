package de.jpaw.bonaparte.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;

@Sharable
public class BonaparteNettyEncoder extends MessageToByteEncoder<BonaPortable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BonaparteNettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, BonaPortable msg, ByteBuf out) throws Exception {
        ByteArrayComposer w = new ByteArrayComposer();
        try {
            // catch any exceptions during serialization, because they might not get logged otherwise
            w.writeRecord(msg);
            LOGGER.debug("Writing object {} as stream of {} bytes", msg.getClass().getCanonicalName(), w.getLength());
            if (LOGGER.isTraceEnabled())
                LOGGER.trace("String is {}", new String(w.getBytes()));   // w.getBytes() is costly as it involves an array copy!
            // output the converted data
            out.writeBytes(w.getBuffer(), 0, w.getLength());
        } catch (Exception e) {
            // http://co-de-generation.blogspot.de/2012/09/slf4j-doesnt-log-exception-stacktrace.html
            LOGGER.error("Exception serializing object of type " + msg.ret$PQON() + ": got exception {}", e);
        }
    }
}
