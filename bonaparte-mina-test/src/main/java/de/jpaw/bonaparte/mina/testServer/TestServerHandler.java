/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package de.jpaw.bonaparte.mina.testServer;

import java.util.concurrent.atomic.AtomicInteger;

//import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;




/**
 * {@link IoHandler} for SumUp server.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class TestServerHandler extends IoHandlerAdapter /*<BonaPortable>*/{

    static AtomicInteger threadSerial = new AtomicInteger(0);
    private AtomicInteger counterInThread = new AtomicInteger(0);
    private final int thisThreadId;
    private final static Logger LOGGER = LoggerFactory.getLogger(TestServerHandler.class);


    TestServerHandler() {
        thisThreadId = threadSerial.incrementAndGet();
    }

    @Override
    public void sessionOpened(IoSession session) {
        LOGGER.info("New incoming channel requested for thread " + thisThreadId);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        Request myRequest = (Request) message;
        Response myResponse = new Response();

        myResponse.setSerialNo(myRequest.getSerialNo());
        myResponse.setUniqueId(myRequest.getUniqueId());
        myResponse.setThreadNo(thisThreadId);
        myResponse.setSerialInThread(counterInThread.incrementAndGet());
        myResponse.setWhenReceiced(new LocalDateTime());

        if (myRequest.getDuration() > 0)
            try {
                Thread.sleep(myRequest.getDuration());
            } catch (InterruptedException e) {
            }

        session.write(myResponse);

    }



    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOGGER.warn("Unexpected exception from downstream.", cause);
        session.close(true);
    }
}
