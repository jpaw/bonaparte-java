
package de.jpaw.bonaparte.mina.testClient;

import java.util.UUID;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

public class ClientSessionHandler extends IoHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientSessionHandler.class);
    private boolean finished;
    Request myRequest ;

    public ClientSessionHandler() {
    }

    public boolean isFinished() {
        return finished;
    }
    @Override
    public void sessionOpened(IoSession session) {

        UUID myUuid = UUID.randomUUID();
        Request myRequest = new Request();
        myRequest.setDuration(1);
        myRequest.setMessage("Hello, World");
        myRequest.setSerialNo(1 * 100000000);
        myRequest.setUniqueId(myUuid);
        this.myRequest = myRequest;
        session.write(myRequest);
    }



    @Override
    public void messageReceived(IoSession session, Object message) {

        LOGGER.debug("messageReceived: "+ message);
        Response myResponse = (Response) message;

        if (myResponse.getSerialNo() != myRequest.getSerialNo())
            throw new IllegalArgumentException("Difference in serial nos "+ myResponse.getSerialNo());


        session.close(true);

        finished = true;


    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close(true);
    }
}
