package de.jpaw.bonaparte.netty;

import javax.net.ssl.SSLEngine;

import de.jpaw.bonaparte.core.BonaPortable;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.ssl.SslHandler;

public class BonaparteNettySslPipelineFactory extends ChannelInitializer<SocketChannel> {
    private final int maximumMessageLength;
    private final ChannelInboundMessageHandlerAdapter<BonaPortable> objectHandler;
    
    public BonaparteNettySslPipelineFactory(int maximumMessageLength, ChannelInboundMessageHandlerAdapter<BonaPortable> objectHandler) {
        this.maximumMessageLength = maximumMessageLength;
        this.objectHandler = objectHandler;
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // create the SSL engine
        SSLEngine engine = NettySslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);
        
        // add ssl to pipeline first, as in the SecureChat example
        pipeline.addLast("ssl",     new SslHandler(engine));
        
        // Add the text line codec combination first,
        pipeline.addLast("framer",  new DelimiterBasedFrameDecoder(maximumMessageLength, false, Delimiters.lineDelimiter()));
        // transmission serialization format
        pipeline.addLast("decoder", new BonaparteNettyDecoder());
        pipeline.addLast("encoder", new BonaparteNettyEncoder());
        // and then business logic.
        pipeline.addLast("handler", objectHandler);
    }
}
