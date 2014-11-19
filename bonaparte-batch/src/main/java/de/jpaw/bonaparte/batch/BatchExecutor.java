package de.jpaw.bonaparte.batch;

public interface BatchExecutor<E,F> extends BatchMainCallback<E>, Contributor {
    void open(BatchProcessorFactory<E,F> processorFactory, BatchWriter<? super F> writer) throws Exception;
    int getNumberOfRecordsTotal();
    int getNumberOfRecordsException();
}
