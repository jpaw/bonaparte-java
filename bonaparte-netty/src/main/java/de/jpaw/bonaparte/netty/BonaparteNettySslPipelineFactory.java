package de.jpaw.bonaparte.netty;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import javax.net.ssl.SSLEngine;

import de.jpaw.bonaparte.core.BonaPortable;

public class BonaparteNettySslPipelineFactory extends ChannelInitializer<SocketChannel> {
    private final static int DEFAULT_NUM_THREADS = 12;
    private final int maximumMessageLength;
    private final SimpleChannelInboundHandler<BonaPortable> objectHandler;
    private final boolean useSsl; // if true, enables SSL, otherwise performs exactly as the non-SSL version
    private final boolean clientMode; // false
    private final boolean needClientAuth; // true
    private final ErrorForwarder errorForwarder;
    private final DefaultEventExecutorGroup databaseWorkerThreadPool;

    public BonaparteNettySslPipelineFactory(int maximumMessageLength, SimpleChannelInboundHandler<BonaPortable> objectHandler, boolean useSsl,
            boolean clientMode, boolean needClientAuth, ErrorForwarder errorForwarder) {
        this.maximumMessageLength = maximumMessageLength;
        this.objectHandler = objectHandler;
        this.useSsl = useSsl;
        this.clientMode = clientMode;
        this.needClientAuth = needClientAuth;
        this.errorForwarder = errorForwarder;
        this.databaseWorkerThreadPool = new DefaultEventExecutorGroup(DEFAULT_NUM_THREADS);
    }

    public BonaparteNettySslPipelineFactory(int maximumMessageLength, SimpleChannelInboundHandler<BonaPortable> objectHandler, boolean useSsl,
            boolean clientMode, boolean needClientAuth, ErrorForwarder errorForwarder, int numThreads) {
        this.maximumMessageLength = maximumMessageLength;
        this.objectHandler = objectHandler;
        this.useSsl = useSsl;
        this.clientMode = clientMode;
        this.needClientAuth = needClientAuth;
        this.errorForwarder = errorForwarder;
        if (numThreads > 1) {
            databaseWorkerThreadPool = new DefaultEventExecutorGroup(numThreads);
        } else {
            databaseWorkerThreadPool = null;
        }
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
        pipeline.addLast("framer", new LineBasedFrameDecoder(maximumMessageLength, false, false));
        // transmission serialization format
        pipeline.addLast("decoder", new BonaparteNettyDecoder(errorForwarder));
        pipeline.addLast("encoder", new BonaparteNettyEncoder());
        // and then business logic.
        if (databaseWorkerThreadPool != null)
            pipeline.addLast(databaseWorkerThreadPool, "handler", objectHandler);        // separate worker pool
        else
            pipeline.addLast("handler", objectHandler);             // do it in the I/O thread
    }
}
