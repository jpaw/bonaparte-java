package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchProcessor;
import de.jpaw.bonaparte.batch.BatchProcessorFactory;
import de.jpaw.bonaparte.batch.ContributorNoop;

/** The ECHO function: returns the parameter. */
public class BatchProcessorFactoryIdentity<X> extends ContributorNoop implements BatchProcessorFactory<X,X> {
    
    @Override
    public BatchProcessor<X, X> getProcessor(int threadNo) {
        return new BatchProcessorIdentity<X>();
    }
    
    static private class BatchProcessorIdentity<X> implements BatchProcessor<X,X> {

        @Override
        public X process(int recordNo, X data) throws Exception {
            return data;
        }

        @Override
        public void close() throws Exception {
        }
    }
}
