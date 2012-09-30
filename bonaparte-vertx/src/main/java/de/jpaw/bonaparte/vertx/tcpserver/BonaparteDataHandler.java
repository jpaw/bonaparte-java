package de.jpaw.bonaparte.vertx.tcpserver;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.net.NetSocket;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class BonaparteDataHandler implements Handler<Buffer> {
    private NetSocket sock;
    
    BonaparteDataHandler(NetSocket sock) {
        this.sock = sock;
    }
    
    @Override
    public void handle(Buffer data) {
        MessageParser<MessageParserException> p = new ByteArrayParser(data.getBytes(), 0, -1);
        try {
            BonaPortable objectIn;
            objectIn = p.readRecord();
            System.out.println("Received an object of type " + objectIn.get$PQON());
            System.out.println(objectIn.toString());
            // send something: just echo it!
            ByteArrayComposer bac = new ByteArrayComposer();
            bac.writeRecord(objectIn);
            byte [] dataOut = bac.getBytes();
            Buffer outputBuffer = new Buffer(dataOut);
            sock.write(outputBuffer);
            
        } catch (MessageParserException e) {
            e.printStackTrace();
        }
    }

}
