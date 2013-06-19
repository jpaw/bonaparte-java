package de.jpaw.bonaparte.hornetq.testSender;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;

public class SimpleUnpooledClient {

    private final InetAddress addr;
    private final Socket conn;
    private final ByteArrayComposer w;
    private byte [] responseBuffer;

    public SimpleUnpooledClient(String queuename, int port) throws IOException {
        addr = InetAddress.getByName(queuename);
        conn = new Socket(addr, port);
        w = new ByteArrayComposer();
        responseBuffer = new byte [10000];
    }

    public BonaPortable doIO(BonaPortable request) throws Exception {
        w.reset();
        w.writeRecord(request);
        conn.getOutputStream().write(w.getBuffer(), 0, w.getLength());
        int numbytes = conn.getInputStream().read(responseBuffer);
        if (numbytes <= 0)
            return null;
        ByteArrayParser p = new ByteArrayParser(responseBuffer, 0, numbytes);
        return p.readRecord();
    }

}
