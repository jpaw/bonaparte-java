package de.jpaw.bonaparte.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import de.jpaw.bonaparte.core.BonaPortable;

public class BonaparteNettyPipelineFactory extends ChannelInitializer<SocketChannel> {
    private final int maximumMessageLength;
    private final SimpleChannelInboundHandler<BonaPortable> objectHandler;
    private final ErrorForwarder errorForwarder;

    public BonaparteNettyPipelineFactory(int maximumMessageLength, SimpleChannelInboundHandler<BonaPortable> objectHandler,
            ErrorForwarder errorForwarder) {
        this.maximumMessageLength = maximumMessageLength;
        this.objectHandler = objectHandler;
        this.errorForwarder = errorForwarder;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast("framer", new LineBasedFrameDecoder(maximumMessageLength, false, false));
        // transmission serialization format
        pipeline.addLast("decoder", new BonaparteNettyDecoder(errorForwarder));
        pipeline.addLast("encoder", new BonaparteNettyEncoder());
        // and then business logic.
        pipeline.addLast("handler", objectHandler);
    }
}
