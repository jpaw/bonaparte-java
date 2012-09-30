package de.jpaw.bonaparte.stomp.testClient;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

public class OneThread implements Runnable {
    private final int threadIndex;
    private final int callsPerThread;
    private final SimpleUnpooledClient conn;

    private Date start;
    private Date stop;

    OneThread(int callsPerThread, int threadIndex, String host) throws IOException {
        this.callsPerThread = callsPerThread;
        this.threadIndex = threadIndex;
        conn = new SimpleUnpooledClient(host, 61613);
    }

    @Override
    public void run() {
        UUID myUuid = UUID.randomUUID();
        Request myRequest = new Request();
        myRequest.setDuration(0);
        myRequest.setMessage("Hello, World");
        myRequest.setSerialNo(threadIndex * 100000000);
        myRequest.setUniqueId(myUuid);

        start = new Date();
        try {
            for (int i = 0; i < callsPerThread; ++i) {
                myRequest.setSerialNo(threadIndex * 100000000 + i);
                conn.doIO(myRequest);
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
