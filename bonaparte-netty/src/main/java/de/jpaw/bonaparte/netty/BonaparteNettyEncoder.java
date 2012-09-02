package de.jpaw.bonaparte.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageEncoder;

@Sharable
public class BonaparteNettyEncoder extends MessageToMessageEncoder<Object, ByteBuf> {
	private static final Logger logger = LoggerFactory.getLogger(BonaparteNettyEncoder.class);
	
	@Override
	public boolean isEncodable(Object msg) throws Exception {
		return msg instanceof BonaPortable;
	}

	@Override
	public ByteBuf encode(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof BonaPortable) {
			ByteArrayComposer w = new ByteArrayComposer();
			w.writeRecord((BonaPortable)w);
			// create a new ByteBuf with the contents of w.getBuffer()
			ByteBuf result = Unpooled.directBuffer(w.getLength());
			result.setBytes(0, w.getBuffer(), 0, w.getLength());
			return result;
		}
		return null;
	}

}
