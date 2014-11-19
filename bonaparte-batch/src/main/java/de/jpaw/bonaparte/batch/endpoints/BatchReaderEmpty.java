package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchReader;
import de.jpaw.bonaparte.batch.BatchMainCallback;
import de.jpaw.bonaparte.batch.ContributorNoop;

/** Batch reader for testing. This one represents an empty source. */
public class BatchReaderEmpty<E> extends ContributorNoop implements BatchReader<E> {
    @Override
    public void produceTo(BatchMainCallback<? super E> whereToPut) {
    }
}
