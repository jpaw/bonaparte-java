package de.jpaw.bonaparte.netty.testClient;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;
import de.jpaw.bonaparte.sock.SimpleTcpClient;

public class OneThread implements Runnable {
    private final int threadIndex;
    private final int delay;
    private final int callsPerThread;
    private final SimpleTcpClient conn;
    private final boolean doRaw;

    private Date start;
    private Date stop;

    OneThread(int delay, int callsPerThread, int threadIndex, String host, int port, boolean useSsl, boolean doRaw) throws IOException {
        this.delay = delay;
        this.callsPerThread = callsPerThread;
        this.threadIndex = threadIndex;
        this.doRaw = doRaw;
        conn = new SimpleTcpClient(host, port, useSsl);
    }

    @Override
    public void run() {
        UUID myUuid = UUID.randomUUID();
        Request myRequest = new Request();
        myRequest.setDuration(delay);
        myRequest.setMessage("Hello, World");
        myRequest.setSerialNo(threadIndex * 100000000);
        myRequest.setUniqueId(myUuid);

        start = new Date();
        try {
            if (doRaw) {
                myRequest.setSerialNo(threadIndex);
                ByteArrayComposer bac = new ByteArrayComposer();
                bac.writeRecord(myRequest);
                byte [] request = bac.getBytes();
                for (int i = 0; i < callsPerThread; ++i) {
                    @SuppressWarnings("unused")
                    byte [] response = conn.doRawIO(request);
                }
            } else {
                for (int i = 0; i < callsPerThread; ++i) {
                    myRequest.setSerialNo((threadIndex * 100000000) + i);
                    BonaPortable response = conn.doIO(myRequest);
                    Response myResponse = (Response) response;
                    if (myResponse.getSerialNo() != myRequest.getSerialNo()) {
                        throw new Exception("Difference in serial nos for thread " + threadIndex + " and loop no " + i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + threadIndex + " did not finish");
            return;
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerMilliSecond = callsPerThread / millis;
        System.out.println("Thread result: " + (int) callsPerMilliSecond + " k calls / second");
    }
}
