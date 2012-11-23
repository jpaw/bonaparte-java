package de.jpaw.bonaparte.netty.testClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;

public class SimpleUnpooledClient {
    private static final Logger logger = LoggerFactory.getLogger(SimpleUnpooledClient.class);

    private final InetAddress addr;
    private final Socket conn;
    private final ByteArrayComposer w;
    private byte [] responseBuffer;

    private static void printSocketInfo(SSLSocket s) {
        logger.info("Socket class: " + s.getClass());
        logger.info("   Remote address = " + s.getInetAddress().toString());
        logger.info("   Remote port = " + s.getPort());
        logger.info("   Local socket address = " + s.getLocalSocketAddress().toString());
        logger.info("   Local address = " + s.getLocalAddress().toString());
        logger.info("   Local port = " + s.getLocalPort());
        logger.info("   Need client authentication = " + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        logger.info("   Cipher suite = " + ss.getCipherSuite());
        logger.info("   Protocol = " + ss.getProtocol());
    }

    public SimpleUnpooledClient(String hostname, int port, boolean useSsl) throws IOException {
        addr = InetAddress.getByName(hostname);
        w = new ByteArrayComposer();
        responseBuffer = new byte [10000];

        if (useSsl) {
            SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
            conn = f.createSocket(addr, port);
            SSLSocket c = (SSLSocket) conn;
            printSocketInfo(c);
            c.startHandshake();
        } else {
            conn = new Socket(addr, port);
        }
    }

    public BonaPortable doIO(BonaPortable request) throws Exception {
        w.reset();
        w.writeRecord(request);
        conn.getOutputStream().write(w.getBuffer(), 0, w.getLength());
        int numbytes = conn.getInputStream().read(responseBuffer);
        if (numbytes <= 0) {
            return null;
        }
        ByteArrayParser p = new ByteArrayParser(responseBuffer, 0, numbytes);
        return p.readRecord();
    }

}
