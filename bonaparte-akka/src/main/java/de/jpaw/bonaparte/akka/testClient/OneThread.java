package de.jpaw.bonaparte.akka.testClient;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.rqrs.Request;
import de.jpaw.bonaparte.pojos.rqrs.Response;

public class OneThread implements Runnable {
    private final int threadIndex;
    private final int delay;
    private final int callsPerThread;
    private final SimpleUnpooledClient conn;

    private Date start;
    private Date stop;

    OneThread(int delay, int callsPerThread, int threadIndex, String host) throws IOException {
        this.delay = delay;
        this.callsPerThread = callsPerThread;
        this.threadIndex = threadIndex;
        conn = new SimpleUnpooledClient(host, 8077);
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
            for (int i = 0; i < callsPerThread; ++i) {
                myRequest.setSerialNo(threadIndex * 100000000 + i);
                BonaPortable response = conn.doIO(myRequest);
                Response myResponse = (Response) response;
                if (myResponse.getSerialNo() != myRequest.getSerialNo())
                    throw new Exception("Difference in serial nos for thread " + threadIndex + " and loop no " + i);
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
