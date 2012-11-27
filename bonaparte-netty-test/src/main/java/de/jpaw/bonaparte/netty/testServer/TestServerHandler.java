/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package de.jpaw.bonaparte.netty.testServer;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.ssl.SslHandler;

import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class TestServerHandler extends ChannelInboundMessageHandlerAdapter<BonaPortable> {
    static AtomicInteger threadSerial = new AtomicInteger(0);
    private AtomicInteger counterInThread = new AtomicInteger(0);
    private final int thisThreadId;

    private static final Logger logger = LoggerFactory.getLogger(TestServerHandler.class);

    TestServerHandler() {
        thisThreadId = threadSerial.incrementAndGet();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New incoming channel requested for thread " + thisThreadId);
        // sslHandler not yet valid here, handshake only starts now!
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel for thread " + thisThreadId + " closed after " + counterInThread.get() + " requests");
        // number of requests is cumulative, as this instance is reused for future new connections
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, BonaPortable request) throws Exception {
        String cipher;
        SslHandler sslH = ctx.pipeline().get(SslHandler.class);
        if (sslH != null) {
            cipher = " (with cipher " + sslH.getEngine().getSession().getCipherSuite() + ")";
        } else {
            cipher = " (unencrypted)";
        }
        logger.info("Received an object of type " + request.getClass().getCanonicalName() + cipher);
        Request myRequest = (Request) request;
        Response myResponse = new Response();

        myResponse.setSerialNo(myRequest.getSerialNo());
        myResponse.setUniqueId(myRequest.getUniqueId());
        myResponse.setThreadNo(thisThreadId);
        myResponse.setSerialInThread(counterInThread.incrementAndGet());
        myResponse.setWhenReceiced(new LocalDateTime());

        if (myRequest.getDuration() > 0) {
            Thread.sleep(myRequest.getDuration());
        }

        ctx.write(myResponse);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
