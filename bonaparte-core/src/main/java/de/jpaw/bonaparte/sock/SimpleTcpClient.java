package de.jpaw.bonaparte.sock;

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

public class SimpleTcpClient {
    private static final Logger logger = LoggerFactory.getLogger(SimpleTcpClient.class);

    private final InetAddress addr;
    private final Socket conn;
    private final ByteArrayComposer w;
    private final byte [] responseBuffer;

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

    public SimpleTcpClient(String hostname, int port, boolean useSsl) throws IOException {
        this(hostname, port, useSsl, 64000);
    }
    
    public SimpleTcpClient(String hostname, int port, boolean useSsl, int bufferSize) throws IOException {
        addr = InetAddress.getByName(hostname);
        w = new ByteArrayComposer();
        responseBuffer = new byte [bufferSize];

        if (useSsl) {
            SSLSocketFactory f = (SSLSocketFactory) SSLSocketFactory.getDefault();
            conn = f.createSocket(addr, port);
            SSLSocket c = (SSLSocket) conn;
            printSocketInfo(c);
            c.startHandshake();
            SSLSession session = c.getSession();
            SessionInfo.logSessionInfo(session, "Server");
        } else {
            conn = new Socket(addr, port);
        }
    }

    public BonaPortable doIO(BonaPortable request) throws Exception {
        boolean foundDelimiter = false; 
        w.reset();
        w.writeRecord(request);
        conn.getOutputStream().write(w.getBuffer(), 0, w.getLength());
        int haveBytes = 0;
        do {
            int numBytes = conn.getInputStream().read(responseBuffer, haveBytes, responseBuffer.length - haveBytes);
            if (numBytes <= 0)
                break;
            for (int i = 0; i < numBytes; ++i) {
                if (responseBuffer[haveBytes+i] == (byte)0x0a) {
                    foundDelimiter = true;
                    break;
                    // fast track: return new ByteArrayParser(responseBuffer, 0, haveBytes+i+1).readRecord();
                }
            }
            haveBytes += numBytes;
        } while (!foundDelimiter);
        if (haveBytes <= 0)
            return null;
        ByteArrayParser p = new ByteArrayParser(responseBuffer, 0, haveBytes);
        return p.readRecord();
    }
    
    // close the connection
    public void close() throws IOException {
        conn.close();
    }

}
