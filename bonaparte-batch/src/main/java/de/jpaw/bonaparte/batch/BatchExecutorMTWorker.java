package de.jpaw.bonaparte.batch;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The batch worker is a process run in parallel in multiple threads which processes
 * F = f(E)
 * and stores the result in the output queue.
 */
public class BatchExecutorMTWorker<E,F> implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(BatchExecutorMTWorker.class);
    
    private final int threadIndex;
    private final BlockingQueue<DataWithOrdinal<E>> inQueue;
    private final BlockingQueue<DataWithOrdinal<F>> outQueue;
    private final BatchProcessor<E,F> processor;
    
    private int numProcessed = 0;
    private int numExceptions = 0;
    private int numError = 0;
    
    public BatchExecutorMTWorker(int threadIndex,
    		BlockingQueue<DataWithOrdinal<E>> inQueue,
    		BlockingQueue<DataWithOrdinal<F>> outQueue,
    		BatchProcessor<E,F> processor) {
    	this.threadIndex = threadIndex;
    	this.inQueue = inQueue;
    	this.outQueue = outQueue;
    	this.processor = processor;
    }
    
    @Override
    public void run() {
    	
    	while (true) {
    		DataWithOrdinal<E> newRecord = null;
    		try {
    			newRecord = inQueue.take();
    		} catch (InterruptedException e) {
    			// interrupt means end of processing, we are done!
    			break;
    		}
    		if (newRecord.recordno.equals(BatchExecutorMultiThreaded.EOF))  // record number -1 means EOF 
    			break;
    		// we got a record
    		++numProcessed;
    		try {
    			F result = processor.process(newRecord.recordno, newRecord.data);
    			outQueue.put(new DataWithOrdinal<F>(newRecord.recordno, result));
    		} catch (Exception e) {
    			++numExceptions;
    		}
    	}
    	LOG.info("Thread {} processed {} records ({} error, {} exceptions)", threadIndex, numProcessed, numError, numExceptions);
    }
}
