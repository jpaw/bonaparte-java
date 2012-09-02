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
        if (msg.hasArray()) {
            final int offset = msg.readerIndex();
            ByteArrayParser p = new ByteArrayParser(msg.array(), msg.arrayOffset() + offset, msg.readableBytes());
            return p.readRecord();
        }
    	return null;
    }

}
