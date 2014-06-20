package de.jpaw.bonaparte.batch;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

import de.jpaw.util.DayTime;

/** Implements the main thread of batch processing, as well as central components such as worker thread creation and termination,
 * buffer allocation, statistics output and more.
 *
 * A specific application must call the mainSub method with appropriate parameters for input, output and processing.
 */

public class BatchMain<E, F> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchMain.class);
    
	// some statistics data
	protected LocalDateTime programStart;
	protected LocalDateTime programEnd;
	protected LocalDateTime parsingStart;
	protected LocalDateTime parsingEnd;
	
	private static double recPerSec(int timeInMillis, int numRecords) {
		return timeInMillis == 0 ? 0.0 : 1000.0 * (double)numRecords / (double)timeInMillis;
	}
	
	public void run(String [] args,
			BatchReader<E> reader,
			BatchWriter<F> writer,
			BatchProcessorFactory<E,F> processorFactory,
			BatchExecutor<E,F> executor) throws Exception {
		programStart = LocalDateTime.now();
		// add the main command line parameters
        SimpleJSAP commandLineOptions = null;
		try {
			commandLineOptions = new SimpleJSAP("Bonaparte batch processor", "Runs batched tasks with multithreading", new Parameter[] {});
		} catch (JSAPException e) {
			LOG.error("Cannot create command line parameters: {}", e);
			System.exit(1);
		}
		// add input / output related options
		reader.addCommandlineParameters(commandLineOptions);
		writer.addCommandlineParameters(commandLineOptions);
		processorFactory.addCommandlineParameters(commandLineOptions);
		executor.addCommandlineParameters(commandLineOptions);
		
        JSAPResult params = commandLineOptions.parse(args);
        if (commandLineOptions.messagePrinted()) {
            System.err.println("(use option --help for usage)");
            System.exit(1);
        }
        
        reader.evalCommandlineParameters(params);
        writer.evalCommandlineParameters(params);
        processorFactory.evalCommandlineParameters(params);
        executor.evalCommandlineParameters(params);
        
        executor.open(processorFactory, writer);
        
		parsingStart = LocalDateTime.now();
		LOG.info("{}, Bonaparte batch: Starting to parse", parsingStart);
		
		reader.produceTo(executor);
		
		parsingEnd = LocalDateTime.now();
		int timediffInMillis = DayTime.LocalDateTimeDifference(parsingStart, parsingEnd);
		int numRecords = executor.getNumberOfRecordsTotal();
		int numExceptions = executor.getNumberOfRecordsException();
		LOG.info("{}, Bonaparte batch: read {} records, total time = {} ms, {} records per second",
				parsingEnd, numRecords, timediffInMillis, recPerSec(timediffInMillis, numRecords));
		
		executor.close();
        
		// We are done. Close the inputs and outputs
		reader.close();				// nothing should happen here...
		writer.close();				// flush and close output files.
		processorFactory.close();  	// close remote connections
		
		programEnd = LocalDateTime.now();
		timediffInMillis = DayTime.LocalDateTimeDifference(programStart, programEnd);
		LOG.info("{}, Bonaparte batch: processed {} records, total time = {} ms, {} records per second, {} exceptions",
				programEnd,numRecords, timediffInMillis, recPerSec(timediffInMillis, numRecords), numExceptions);
	}
	
	// shorthand to save an arg
	public void runST(String [] args,
			BatchReader<E> reader,
			BatchWriter<F> writer,
			BatchProcessorFactory<E,F> processorFactory) throws Exception {
		run(args, reader, writer, processorFactory, new BatchExecutorUnthreaded<E, F>());
	}
	
	// shorthand to save an arg
	public void runMT(String [] args,
			BatchReader<E> reader,
			BatchWriter<F> writer,
			BatchProcessorFactory<E,F> processorFactory) throws Exception {
		run(args, reader, writer, processorFactory, new BatchExecutorMultiThreaded<E, F>());
	}
	
    // shorthand to save an arg
    public void run3T(String [] args,
            BatchReader<E> reader,
            BatchWriter<F> writer,
            BatchProcessorFactory<E,F> processorFactory) throws Exception {
        run(args, reader, writer, processorFactory, new BatchExecutor3Threads<E, F>());
    }
}
