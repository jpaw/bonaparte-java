package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchWriter;
import de.jpaw.bonaparte.batch.ContributorNoop;

public class BatchWriterDevNull<E> extends ContributorNoop implements BatchWriter<E> {

    @Override
    public void storeResult(int no, E response) {
    }
}
