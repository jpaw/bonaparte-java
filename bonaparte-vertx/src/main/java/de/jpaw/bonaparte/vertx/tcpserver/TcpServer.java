package de.jpaw.bonaparte.vertx.tcpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.Vertx;

public class TcpServer {
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.newVertx();
        NetServer server = vertx.createNetServer();

        server.connectHandler(new BonaparteConnectHandler());
/*
        server.connectHandler(new Handler<NetSocket>() {
            public void handle(NetSocket sock) {
                logger.info("A client has connected!");
                sock.dataHandler(new BonaparteDataHandler(sock));
            }
        }) */
        server.listen(42424, "localhost");
        server.setReceiveBufferSize(64000);
        server.setSendBufferSize(64000);
        server.setReuseAddress(true);
        server.setTCPNoDelay(true);  // disable Nagle algorithm. We transmit full messages only.
        server.setAcceptBacklog(100);

        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
