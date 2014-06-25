package de.jpaw.bonaparte.netty.testClient;

import java.util.Date;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;

public class NettyBenchmark {
    public static void main(String[] args) throws Exception {
        int numberOfThreads = 1;
        int callsPerThread = 1;
        int delay = 0;
        int port = 8077;
        boolean useSsl = false;
        boolean doRaw = false;
        Thread threads[];
        Date start;
        Date stop;
        String hostname = "localhost";

        SimpleJSAP commandLineOptions = new SimpleJSAP("NettyBenchmark", "Runs a small non-SSL netty benchmark", new Parameter[] {
                new FlaggedOption("delay", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, 'd', "delay", "delay per execution in ms"),
                new FlaggedOption("threads", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 't', "threads", "number of threads"),
                new FlaggedOption("calls", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 'n', "calls", "number of calls per thread"),
                new FlaggedOption("host", JSAP.STRING_PARSER, "localhost", JSAP.NOT_REQUIRED, 'h', "host", "remote host name or IP address"),
                new FlaggedOption("port", JSAP.INTEGER_PARSER, "8077", JSAP.NOT_REQUIRED, 'p', "port", "remote listener port"),
                new Switch("raw", 'r', "raw", "send prebaked requests, so not parse responses"),
                new Switch("ssl", 's', "ssl", "enforces SSL connection") });
        JSAPResult cmd = commandLineOptions.parse(args);
        if (commandLineOptions.messagePrinted()) {
            System.err.println("(use option --help for usage)");
            System.exit(1);
        }

        delay = cmd.getInt("delay");
        numberOfThreads = cmd.getInt("threads");
        callsPerThread = cmd.getInt("calls");
        hostname = cmd.getString("host");
        port = cmd.getInt("port");
        useSsl = cmd.getBoolean("ssl");
        doRaw = cmd.getBoolean("raw");

        System.out.println("Starting " + (useSsl ? "SSL" : "plain") + " benchmark with delay " + delay + " ms with " + numberOfThreads + " threads and "
                + callsPerThread
                + " calls per thread to host " + hostname + ", port " + port);

        start = new Date();
        threads = new Thread[numberOfThreads];

        // start all the threads...
        for (int i = 0; i < numberOfThreads; ++i) {
            Runnable worker = new OneThread(delay, callsPerThread, i, hostname, port, useSsl, doRaw);
            threads[i] = new Thread(worker);
            threads[i].start();
        }
        // wait for the threads to finish...
        for (int i = 0; i < numberOfThreads; ++i) {
            threads[i].join();
        }
        stop = new Date();
        long millis = stop.getTime() - start.getTime();
        double callsPerMilliSecond = (callsPerThread * numberOfThreads) / millis;
        System.out.println("Overall result: " + (int) callsPerMilliSecond + " k calls / second");
    }

}
