package de.jpaw.bonaparte.batch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import de.jpaw.bonaparte.sock.SessionInfo;

public class BatchProcessorFactoryTcp<X> implements BatchProcessorFactory<X,X> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchProcessorFactoryTcp.class);
    private final BatchProcessorMarshaller<X> marshaller;
    private int bufferSize = 1024 * 1024;
    private int port = 80;
    private boolean useSsl = false;
    private InetAddress addr;
    
    public BatchProcessorFactoryTcp(BatchProcessorMarshaller<X> marshaller) {
        this.marshaller = marshaller;
    }
    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'H', "host", "remote host name or IP address"));
        params.registerParameter(new FlaggedOption("port", JSAP.INTEGER_PARSER, "80", JSAP.NOT_REQUIRED, 'P', "port", "server TCP/IP port"));
        params.registerParameter(new Switch("ssl", 'S', "ssl", "use SSL"));
        params.registerParameter(new FlaggedOption("buffersize", JSAP.INTEGER_PARSER, "1000000", JSAP.NOT_REQUIRED, 'B', "rest-buffer-size", "buffer size for REST requests"));
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        bufferSize = params.getInt("buffersize");
        port = params.getInt("port");
        useSsl = params.getBoolean("ssl");
        String host = params.getString("host");
        addr = InetAddress.getByName(host);
    }

    @Override
    public void close() throws Exception {
    }
    
    private static void printSocketInfo(SSLSocket s) {
        LOG.info("Socket class: " + s.getClass());
        LOG.info("   Remote address = " + s.getInetAddress().toString());
        LOG.info("   Remote port = " + s.getPort());
        LOG.info("   Local socket address = " + s.getLocalSocketAddress().toString());
        LOG.info("   Local address = " + s.getLocalAddress().toString());
        LOG.info("   Local port = " + s.getLocalPort());
        LOG.info("   Need client authentication = " + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        LOG.info("   Cipher suite = " + ss.getCipherSuite());
        LOG.info("   Protocol = " + ss.getProtocol());
    }

    @Override
    public BatchProcessor<X,X> getProcessor(int threadNo) throws IOException {
        // connect and then return the new processor
        Socket conn = null;
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
        return new BatchProcessorTcp<X>(bufferSize, marshaller, conn);
    }
    
    private static class BatchProcessorTcp<X> implements BatchProcessor<X,X> {
        private final Socket conn;
        private final byte [] responseBuffer;
        private final BatchProcessorMarshaller<X> marshaller;
        
        private BatchProcessorTcp(int bufferSize, BatchProcessorMarshaller<X> marshaller, Socket conn) {
            responseBuffer = new byte [bufferSize];
            this.marshaller = marshaller;
            this.conn = conn;
        }
        
        @Override
        public X process(int recordNo, X data) throws Exception {
            // get the raw data
            boolean foundDelimiter = false; 
//          byte [] payload = marshaller.marshal(data);
//          conn.getOutputStream().write(payload);
            marshaller.marshal(data, conn.getOutputStream());
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
            
            return marshaller.unmarshal(responseBuffer, haveBytes);
        }

        @Override
        public void close() throws Exception {      // nothing to do, REST is connectionless
            conn.close();
        }
    }

}
