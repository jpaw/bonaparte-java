package de.jpaw.bonaparte.sock;

import java.io.IOException;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.socket.SimpleRawTcpClient;

public class SimpleTcpClient extends SimpleRawTcpClient {
    private final ByteArrayComposer w;

    public SimpleTcpClient(String hostname, int port, boolean useSsl) throws IOException {
        this(hostname, port, useSsl, 64000);
    }

    public SimpleTcpClient(String hostname, int port, boolean useSsl, int bufferSize) throws IOException {
        super(hostname, port, useSsl, bufferSize);
        w = new ByteArrayComposer();
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

}
