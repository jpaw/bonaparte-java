package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.bonaparte.batch.BatchReader;
import de.jpaw.bonaparte.batch.BatchReaderTextFileAbstract;
import de.jpaw.bonaparte.batch.BatchMainCallback;

public class BatchReaderTextFile extends BatchReaderTextFileAbstract implements BatchReader<String> {

    @Override
    public void produceTo(BatchMainCallback<String> whereToPut) throws Exception {
        String line;
        while ((line = getNext()) != null) {
            whereToPut.scheduleForProcessing(line);
        }
    }
}
