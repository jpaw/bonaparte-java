package de.jpaw.bonaparte.netty.testClient;

import java.util.Date;

public class NettyBenchmark {
    public static void main(String[] args) throws Exception {
        int numberOfThreads = 1;
        int callsPerThread = 1;
        int delay = 0;
        Thread threads[];
        Date start;
        Date stop;
        String hostname = "localhost";

        if (args.length > 0) {
            delay = Integer.valueOf(args[0]);
        } else {
            System.out.println("Usage: Benchmark (delay in ms) [(threads) [(calls / thread) [(remotehost)]]]");
            System.exit(1);
        }
        if (args.length > 1) {
            numberOfThreads = Integer.valueOf(args[1]);
        }
        if (args.length > 2) {
            callsPerThread = Integer.valueOf(args[2]);
        }
        if (args.length > 3) {
            hostname = args[3];
        }

        System.out.println("Starting benchmark with delay " + delay + " ms with " + numberOfThreads + " threads and "
        + callsPerThread + " calls per thread to host " + hostname);

        start = new Date();
        threads = new Thread[numberOfThreads];

        // start all the threads...
        for (int i = 0; i < numberOfThreads; ++i) {
            Runnable worker = new OneThread(delay, callsPerThread, i, hostname);
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
