package de.jpaw.bonaparte.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class BonaparteToByteEncoder extends MessageToByteEncoder<BonaPortable> {
    private static final Logger logger = LoggerFactory.getLogger(BonaparteToByteEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, BonaPortable msg, ByteBuf out) throws Exception {
        ByteArrayComposer w = new ByteArrayComposer();
        w.writeRecord((BonaPortable) msg);
        logger.trace("Writing object {} with contents {}", msg.getClass().getCanonicalName(), new String(w.getBytes()));
        // create a new ByteBuf with the contents of w.getBuffer()
        out.setBytes(0, w.getBuffer(), 0, w.getLength());
    }
}
