package de.jpaw.bonaparte.netty;

import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import de.jpaw.bonaparte.core.BonaPortable;

public class BonaparteNettySslPipelineFactory extends ChannelInitializer<SocketChannel> {
    private final int maximumMessageLength;
    private final ChannelInboundMessageHandlerAdapter<BonaPortable> objectHandler;
    private final boolean useSsl; // if true, enables SSL, otherwise performs exactly as the non-SSL version
    private final boolean clientMode; // false
    private final boolean needClientAuth; // true
    private final ErrorForwarder errorForwarder;

    public BonaparteNettySslPipelineFactory(int maximumMessageLength, ChannelInboundMessageHandlerAdapter<BonaPortable> objectHandler, boolean useSsl,
            boolean clientMode, boolean needClientAuth, ErrorForwarder errorForwarder) {
        this.maximumMessageLength = maximumMessageLength;
        this.objectHandler = objectHandler;
        this.useSsl = useSsl;
        this.clientMode = clientMode;
        this.needClientAuth = needClientAuth;
        this.errorForwarder = errorForwarder;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (useSsl) {
            // create the SSL engine
            SSLEngine engine = NettySslContextFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(clientMode);
            engine.setNeedClientAuth(needClientAuth);

            // add ssl to pipeline first, as in the SecureChat example
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        // Add the text line codec combination first,
        pipeline.addLast("framer",  new DelimiterBasedFrameDecoder(maximumMessageLength, false, Delimiters.lineDelimiter()));
        // transmission serialization format
        pipeline.addLast("decoder", new BonaparteNettyDecoder(errorForwarder));
        pipeline.addLast("encoder", new BonaparteNettyEncoder());
        // and then business logic.
        pipeline.addLast("handler", objectHandler);
    }
}
