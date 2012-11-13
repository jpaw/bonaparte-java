package de.jpaw.bonaparte.hornetq.testSender;

import java.util.Date;

public class HornetQBenchmark {
    public static void main(String[] args) throws Exception {
        int numberOfThreads = 1;
        int callsPerThread = 1;
        boolean doObj = false;
        Thread threads[];
        Date start;
        Date stop;
        String queuename = "exampleQueue";

        if (args.length > 0) {
            doObj = args[0].equals("obj");
        } else {
            System.out.println("Usage: Benchmark (obj) [(threads) [(calls / thread) [(queuename)]]]");
            System.exit(1);
        }
        if (args.length > 1) {
            numberOfThreads = Integer.valueOf(args[1]);
        }
        if (args.length > 2) {
            callsPerThread = Integer.valueOf(args[2]);
        }
        if (args.length > 3) {
            queuename = args[3];
        }

        System.out.println("Starting benchmark with queue " + doObj + " with " + numberOfThreads + " threads and "
        + callsPerThread + " calls per thread to queue " + queuename);

        start = new Date();
        threads = new Thread[numberOfThreads];

        // start all the threads...
        for (int i = 0; i < numberOfThreads; ++i) {
            Runnable worker = new OneThread(doObj, callsPerThread, i, queuename);
            threads[i] = new Thread(worker);
            threads[i].start();
        }
        // wait for the threads to finish...
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i].join();
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerSecond = callsPerThread * numberOfThreads * 1000 / millis;
        System.out.println("Overall result: " + (int) callsPerSecond + " calls / second");
    }

}
