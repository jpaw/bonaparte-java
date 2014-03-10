package de.jpaw.bonaparte.batch.endpoints;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.bonaparte.batch.BatchReader;
import de.jpaw.bonaparte.batch.BatchMainCallback;

/** Batch reader for testing. This one repeats the provided input a specified number of times,
 *  and optionally waits between data production. */
public class BatchReaderRepeater<E> implements BatchReader<E> {
	private final E objectToRepeat;
	private int numRepeats = 1;
	private int delayInMillis = 0;
	
	public BatchReaderRepeater(E objectToRepeat) {
		this.objectToRepeat = objectToRepeat;
	}
	
	@Override
	public void addCommandlineParameters(JSAP params) throws Exception {
		params.registerParameter(new FlaggedOption("num", JSAP.INTEGER_PARSER, "1", JSAP.NOT_REQUIRED, 'n', "num", "number of repetitions"));
		params.registerParameter(new FlaggedOption("indelay", JSAP.INTEGER_PARSER, "0", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "in-delay", "number of ms delay between repetitions"));
	}

	@Override
	public void evalCommandlineParameters(JSAPResult params) throws Exception {
		delayInMillis = params.getInt("indelay");
		numRepeats = params.getInt("num");
	}

	@Override
	public void produceTo(BatchMainCallback<E> whereToPut) throws Exception {
		for (int i = 0; i < numRepeats; ++i) {
			Thread.sleep(delayInMillis);
			whereToPut.scheduleForProcessing(objectToRepeat);
		}
	}
	
	@Override
	public void close() throws Exception {
	}
}
