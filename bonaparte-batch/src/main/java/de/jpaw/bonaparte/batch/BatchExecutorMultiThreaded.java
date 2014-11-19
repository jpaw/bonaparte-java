package de.jpaw.bonaparte.batch;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

public class BatchExecutorMultiThreaded<E,F> implements BatchExecutor<E,F> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchExecutorMultiThreaded.class);
    public static int EOF = -1;
    
    private int numberOfThreads = 1;
    private int inQueueSize = 100;
    private int outQueueSize = 100;
    private long timeout = 300;
    private int numRecords = 0;         // number of records read (added to input queue)
    private int numExceptions = 0;      // number of records which could not be scheduled

    private BlockingQueue<DataWithOrdinal<E>> inputQueue = null; 
    private BlockingQueue<DataWithOrdinal<F>> outputQueue = null;
    private BatchExecutorMTResultCollector<? super F> collector = null;
    private Thread collectorThread = null;
    private Thread [] workerThreads = null;

//  protected AtomicInteger numError = new AtomicInteger(0);
//  protected AtomicInteger numProcessed = new AtomicInteger(0);  // number of records processed (good and error records)
//  protected AtomicInteger numExceptions = new AtomicInteger(0);  // number of records which resulted in an exception (severe problem)

    // if worker threads are desired, create
    @Override
    public void open(BatchProcessorFactory<E,F> processorFactory, BatchWriter<? super F> writer) throws Exception {
        inputQueue = new ArrayBlockingQueue<DataWithOrdinal<E>>(inQueueSize);
        outputQueue = new ArrayBlockingQueue<DataWithOrdinal<F>>(outQueueSize);

        collector = new BatchExecutorMTResultCollector<F>(outputQueue, writer);
        collectorThread = new Thread(collector);
        collectorThread.start();

        workerThreads = new Thread[numberOfThreads];
        // create and start all the threads...
        for (int i = 0; i < numberOfThreads; ++i) {
            Runnable worker = new BatchExecutorMTWorker<E, F>(i, inputQueue, outputQueue, processorFactory.getProcessor(i));
            workerThreads[i] = new Thread(worker);
            workerThreads[i].start();
        }
    }
    
    @Override
    public void close() throws Exception {
        // store the EOFs... (1 record of recordNo -1 per thread)
        DataWithOrdinal<E> eof = new DataWithOrdinal<E>(EOF, null);
        for (int i = 0; i < numberOfThreads; ++i) {
            storeToInputQueueWithTimeout(eof);
        }

        // now wait for the threads to process and finish...
        for (int i = 0; i < numberOfThreads; ++i) {
            workerThreads[i].join();
        }

        // then push the EOF for the collector to the output queue...
        outputQueue.put(new DataWithOrdinal<F>(EOF, null));

        // and wait for writer to finish...
        collectorThread.join();
    }
    
    private void storeToInputQueueWithTimeout(DataWithOrdinal<E> record) throws InterruptedException, TimeoutException {
        // inputQueue.put(record);
        boolean couldDoIt = inputQueue.offer(record, timeout, TimeUnit.SECONDS);
        if (!couldDoIt) {
            LOG.error("Timeout occured trying to add record {} to the processing queue", numRecords);
            throw new TimeoutException("Couldn't store record " + record.recordno + " within " + timeout + " seconds");
        }
    }
    
    // BatchMainCallback
    @Override
    public void scheduleForProcessing(E record) {
        ++numRecords;
        try {
            DataWithOrdinal<E> newRecord = new DataWithOrdinal<E>(numRecords, record);
            storeToInputQueueWithTimeout(newRecord);        // let the worker threads pick up the work
        } catch (Exception e) {
            ++numExceptions;
        }
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("threads", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 't', "threads", "number of worker threads (0 = single thread)"));
        params.registerParameter(new FlaggedOption("inqsize", JSAP.INTEGER_PARSER, "100", JSAP.NOT_REQUIRED, 'q', "inqueue-size", "size of input queue buffer (default 100)"));
        params.registerParameter(new FlaggedOption("outqsize", JSAP.INTEGER_PARSER, "100", JSAP.NOT_REQUIRED, 'Q', "outqueue-size", "size of output queue buffer (default 100)"));
        params.registerParameter(new FlaggedOption("timeout", JSAP.LONG_PARSER, "300", JSAP.NOT_REQUIRED, 'w', "max-wait", "maximum wait time per record, before a timeout occurs, in seconds, default 300 (5 minutes)"));
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        numberOfThreads = params.getInt("threads");
        inQueueSize = params.getInt("inqsize");
        outQueueSize = params.getInt("outqsize");
        timeout = params.getLong("timeout");
        if (numberOfThreads <= 0) {
            LOG.error("Bad number of threads. Must have at least 1 worker thread!");
            numberOfThreads = 1;
        }
    }

    @Override
    public int getNumberOfRecordsTotal() {
        return numRecords;
    }
    @Override
    public int getNumberOfRecordsException() {
        return numExceptions;
    }
}
