package de.jpaw.bonaparte.stomp.testClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.util.ByteArray;

public class SimpleUnpooledClient {
    static private final String QUEUE_NAME = "mbi.test";
    static private final byte [] STOMP_HEADER = ("SEND\ndestination:/queue/" + QUEUE_NAME + "\nreceipt:42\n\n").getBytes();
    static private final byte [] STOMP_FOOTER = ("\0").getBytes();
    static private final byte [] EXPECTED_RESPONSE = ("RECEIPT\nreceipt-id:42\n\n\0\n").getBytes();
    
    private final InetAddress addr;
    private final Socket conn;
    private final ByteArrayComposer w;
    private byte [] responseBuffer;
    
    public SimpleUnpooledClient(String hostname, int port) throws IOException {
        addr = InetAddress.getByName(hostname);
        conn = new Socket(addr, port);
        w = new ByteArrayComposer();
        responseBuffer = new byte [10000];
        connect();
    }
    
    public void connect() throws IOException {
        byte [] connectString = "CONNECT\nlogin:admin\npasscode:password\n\n\0".getBytes();
        conn.getOutputStream().write(connectString);
        int numbytes = conn.getInputStream().read(responseBuffer);
        if (numbytes <= 0) {
            return;
        }
        ByteArray r = new ByteArray(responseBuffer, 0, numbytes);
        System.out.println("Received " + numbytes + " bytes response:\n" + new String(r.getBytes()) + "***");
    }
    
    public void doIO(BonaPortable request) throws Exception {
        w.reset();
        // add STOMP protocol header
        w.addRawData(STOMP_HEADER);
        // add the request data
        w.writeRecord(request);
        // add STOMP protocol footer
        w.addRawData(STOMP_FOOTER);
        conn.getOutputStream().write(w.getBuffer(), 0, w.getLength());
        int numbytes = conn.getInputStream().read(responseBuffer);
        if (numbytes <= 0) {
            throw new IOException("Did not get any response");
        }
        byte [] r = (new ByteArray(responseBuffer, 0, numbytes)).getBytes();
        if (Arrays.equals(r, EXPECTED_RESPONSE)) {
            return;
        }
        assert r.length == numbytes : "array length not as expected";
        System.out.println("Received " + numbytes + " bytes response (expected " + EXPECTED_RESPONSE.length + "):\n" + new String(r) + "***");
        if (r.length == EXPECTED_RESPONSE.length) {
            for (int i = 0; i < r.length; ++i) {
                if (r[i] != EXPECTED_RESPONSE[i]) {
                    System.out.println(String.format("Difference at byte %02d: got 0x%02x, expected 0x%02x", i, 0xff & r[i], 0xff & EXPECTED_RESPONSE[i]));
                }
            }
        }
        throw new IOException("Did not get expected response");
    }
    
}
