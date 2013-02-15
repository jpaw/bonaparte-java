package de.jpaw.bonaparte.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;

@Sharable
public class BonaparteNettyEncoder extends MessageToMessageEncoder<BonaPortable> {
    private static final Logger logger = LoggerFactory.getLogger(BonaparteNettyEncoder.class);

    @Override
    public MessageBuf<BonaPortable> newOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
        return Unpooled.messageBuffer();
    }

    @Override
    public ByteBuf encode(ChannelHandlerContext ctx, BonaPortable msg) throws Exception {
        ByteArrayComposer w = new ByteArrayComposer();
        w.writeRecord(msg);
        logger.debug("Writing object {} as stream of {} bytes", msg.getClass().getCanonicalName(), w.getLength());
        logger.trace("String is {}", new String(w.getBytes()));
        // create a new ByteBuf with the contents of w.getBuffer()
        return Unpooled.wrappedBuffer(w.getBuffer(), 0, w.getLength());
    }

}
