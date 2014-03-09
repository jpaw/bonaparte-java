package de.jpaw.bonaparte.batch;

public class BatchExecutorUnthreaded<E,F> extends ContributorNoop implements BatchExecutor<E,F> {
	private BatchProcessor<E,F> localProcessor = null;
	private BatchWriter<F> localWriter = null; 
	private int numRecords = 0;			// number of records read (added to input queue)
	
	@Override
	public void open(BatchProcessorFactory<E, F> processorFactory, BatchWriter<F> writer) throws Exception {
		localProcessor = processorFactory.getProcessor(0);
		localWriter = writer;
	}

	@Override
	public void close() throws Exception {
		localProcessor.close();		
	}
	
	@Override
	public void scheduleForProcessing(E record) throws Exception {  // called by the reader
		++numRecords;
		// process it immediately
		F result = localProcessor.process(numRecords, record);
		// and write the output to the writer
		localWriter.storeResult(numRecords, result);
	}

	@Override
	public int getNumberOfRecordsRead() {
		return numRecords;
	}
}
