package de.jpaw.bonaparte.batch;

public interface BatchProcessorFactory<E,F> extends Contributor {
	BatchProcessor<E,F> getProcessor(int threadNo);
}
