package de.jpaw.bonaparte.stomp.testClient;

import java.util.Date;

public class StompEnqueueBenchmark {
    public static void main(String[] args) throws Exception {
        int numberOfThreads = 1;
        int callsPerThread = 1;
        Thread threads[];
        Date start;
        Date stop;
        String hostname = "localhost";

        if (args.length > 0) {
            numberOfThreads = Integer.valueOf(args[0]);
        } else {
            System.out.println("Usage: Benchmark (threads) [(calls / thread) [(remotehost)]]");
            // System.exit(1);
        }
        if (args.length > 1) {
            callsPerThread = Integer.valueOf(args[1]);
        }
        if (args.length > 2) {
            hostname = args[2];
        }

        System.out.println("Starting enqueuing benchmark with " + numberOfThreads + " threads and "
           + callsPerThread + " calls per thread on host " + hostname);

        start = new Date();
        threads = new Thread[numberOfThreads];

        // start all the threads...
        for (int i = 0; i < numberOfThreads; ++i) {
            Runnable worker = new OneThread(callsPerThread, i, hostname);
            threads[i] = new Thread(worker);
            threads[i].start();
        }
        // wait for the threads to finish...
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i].join();
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerMilliSecond = callsPerThread * numberOfThreads / millis;
        System.out.println("Overall result: " + (int) callsPerMilliSecond + " k calls / second");
    }

}
