package de.jpaw.bonaparte.batch;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

public class BatchExecutor3Threads<E,F> implements BatchExecutor<E,F> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchExecutor3Threads.class);

    
    private static class DWOFactory<T> implements EventFactory<DataWithOrdinal<T>> {

        @Override
        public DataWithOrdinal<T> newInstance() {
            return new DataWithOrdinal<T>(0, null);
        }
    }
    private final EventFactory<DataWithOrdinal<E>> inFactory = new DWOFactory<E>();
    
    
//    private final EventFactory<DataWithOrdinal<E>> inFactory;
//    private final EventFactory<DataWithOrdinal<F>> outFactory;
//    
//    public BatchExecutor3Threads(EventFactory<DataWithOrdinal<E>> inFactory, EventFactory<DataWithOrdinal<F>> outFactory) {
//        this.inFactory = inFactory;
//        this.outFactory = outFactory;
//    }

    private BatchProcessor<E,F> localProcessor = null;
    private BatchWriter<F> localWriter = null; 
    
    private int inBufferSize = 1024;
    private int outBufferSize = 1024;
//    private long timeout = 300;
    private int numRecords = 0;         // number of records read (added to input queue)
    private int numExceptions = 0;      // number of records which could not be scheduled

    Disruptor<DataWithOrdinal<E>> disruptorIn;
    
    RingBuffer<DataWithOrdinal<E>> ringBufferIn = null;

    private FirstEventHandler hdlr1 = null;
    
    private class SecondEventHandler implements EventHandler<DataWithOrdinal<F>> {
        private final BatchWriter<F> localWriter;

        private SecondEventHandler(BatchWriter<F> localWriter) {
            this.localWriter = localWriter;
        }
        
        @Override
        public void onEvent(DataWithOrdinal<F> data, long sequence, boolean isLast) throws Exception {
            // and write the output to the writer
            localWriter.storeResult(data.recordno, data.data);
        }
        
    }

    
//    private BlockingQueue<DataWithOrdinal<E>> inputQueue = null; 
//    private BlockingQueue<DataWithOrdinal<F>> outputQueue = null;
//    private BatchExecutorMTResultCollector<F> collector = null;
//    private Thread collectorThread = null;
//    private Thread [] workerThreads = null;

    
    private class FirstEventHandler implements EventHandler<DataWithOrdinal<E>> {
        private final BatchProcessor<E,F> localProcessor;
        private int numRecords = 0;         // number of records read (added to input queue)
        private int numExceptions = 0;      // number of records which could not be scheduled
        private final EventFactory<DataWithOrdinal<F>> outFactory = new DWOFactory<F>();
        private int outBufferSize = 1024;
        Disruptor<DataWithOrdinal<F>> disruptorOut;
        RingBuffer<DataWithOrdinal<F>> ringBufferOut = null;
        
        private FirstEventHandler(BatchProcessor<E,F> localProcessor, BatchWriter<F> localWriter, int bs) {
            this.localProcessor = localProcessor;
            this.outBufferSize = bs;
            
            // create the output ring buffer
            // Executor that will be used to construct new threads for consumers
            Executor executorPoolOut = Executors.newCachedThreadPool();

            // Construct the Disruptor which interfaces the decoder to DB storage
            disruptorOut = new Disruptor<DataWithOrdinal<F>>(outFactory, outBufferSize, executorPoolOut);

            // Connect the handler
            disruptorOut.handleEventsWith(new SecondEventHandler(localWriter));

            // Start the Disruptor, starts all threads running
            // and get the ring buffer from the Disruptor to be used for publishing.
            ringBufferOut = disruptorOut.start();
        }
        
        private void close() {
            LOG.info("Output buffer will be shut down");
            disruptorOut.shutdown();
            LOG.info("Output buffer has been shut down");
        }

        @Override
        public void onEvent(DataWithOrdinal<E> data, long sequence, boolean isLast) throws Exception {
            // process the data and push it into the second disruptor
            ++numRecords;
            try {
                // process it immediately
                F result = localProcessor.process(data.recordno, data.data);

                long sequence2 = ringBufferOut.next();  // Grab the next sequence
                try {
                    DataWithOrdinal<F> event = ringBufferOut.get(sequence); // Get the entry in the Disruptor for the sequence
                    event.recordno = numRecords;
                    event.data = result;                             // fill data
                } finally {
                    ringBufferOut.publish(sequence2);
                }
            
            } catch (Exception e) {
                ++numExceptions;
            }
        }
    }
    
    // if worker threads are desired, create
    @Override
    public void open(BatchProcessorFactory<E,F> processorFactory, BatchWriter<F> writer) throws Exception {
        this.localWriter = writer;
        this.localProcessor = processorFactory.getProcessor(0);
        
        // Executor that will be used to construct new threads for consumers
        Executor executorPoolIn = Executors.newCachedThreadPool();

        // Construct the Disruptor which interfaces the decoder to DB storage
        disruptorIn = new Disruptor<DataWithOrdinal<E>>(inFactory, inBufferSize, executorPoolIn);

        // Connect the handler
        hdlr1 = new FirstEventHandler(localProcessor, writer, outBufferSize);
        disruptorIn.handleEventsWith(hdlr1);

        // Start the Disruptor, starts all threads running
        // and get the ring buffer from the Disruptor to be used for publishing.
        ringBufferIn = disruptorIn.start();
        LOG.info("Input buffer has been started");
    }
    
    @Override
    public void close() throws Exception {
        LOG.info("Input buffer will be shut down");
        disruptorIn.shutdown();
        LOG.info("Input buffer has been shut down");
        hdlr1.close();
    }
    
    // BatchMainCallback. Calls to this procedure feed the input disruptor
    @Override
    public void scheduleForProcessing(E record) {
        ++numRecords;
        
        long sequence = ringBufferIn.next();  // Grab the next sequence
        try {
            DataWithOrdinal<E> event = ringBufferIn.get(sequence); // Get the entry in the Disruptor for the sequence
            event.recordno = numRecords;
            event.data = record;                             // fill data
        } catch (Exception e) {
            ++numExceptions;
        } finally {
            ringBufferIn.publish(sequence);
        }
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new FlaggedOption("inbsize", JSAP.INTEGER_PARSER, "1024", JSAP.NOT_REQUIRED, 'q', "inbuffer-size", "size of input ring buffer (default 1024)"));
        params.registerParameter(new FlaggedOption("outbsize", JSAP.INTEGER_PARSER, "1024", JSAP.NOT_REQUIRED, 'Q', "outbuffer-size", "size of output ring buffer (default 1024)"));
//      params.registerParameter(new FlaggedOption("timeout", JSAP.LONG_PARSER, "300", JSAP.NOT_REQUIRED, 'w', "max-wait", "maximum wait time per record, before a timeout occurs, in seconds, default 300 (5 minutes)"));
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
//        timeout = params.getLong("timeout");
        inBufferSize = params.getInt("inbsize");
        outBufferSize = params.getInt("outbsize");
        if (inBufferSize <= 0 || (inBufferSize & (inBufferSize-1)) != 0) {
            LOG.error("Input buffer size must be a power of 2");
            inBufferSize = 1024;
        }
        if (outBufferSize <= 0 || (outBufferSize & (outBufferSize-1)) != 0) {
            LOG.error("Output buffer size must be a power of 2");
            outBufferSize = 1024;
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
