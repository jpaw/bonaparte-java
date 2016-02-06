package de.jpaw.bonaparte.vertx.tcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.net.NetSocket;

public class BonaparteConnectHandler implements Handler<NetSocket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BonaparteConnectHandler.class);

    @Override
    public void handle(NetSocket sock) {
        LOGGER.info("A client has connected!");
        //Handler<Buffer> hd = new BonaparteDataHandler(sock);

        sock.dataHandler(new BonaparteDataHandler(sock));
    }

}
