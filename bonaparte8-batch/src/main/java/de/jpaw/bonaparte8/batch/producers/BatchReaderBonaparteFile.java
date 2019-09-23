package de.jpaw.bonaparte8.batch.producers;

import java.util.function.ObjIntConsumer;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw8.batch.producers.impl.BatchReaderTextFileAbstract;

public class BatchReaderBonaparteFile extends BatchReaderTextFileAbstract<BonaPortable> {

    @Override
    public void produceTo(ObjIntConsumer<? super BonaPortable> whereToPut) throws Exception {
        int n = 0;
        StringBuilder buffer = new StringBuilder(8000);  // have a buffer which persists a bit longer to avoid GC overhead

        // while data is available, insert it into the queue
        String line;
        while ((line = getNext()) != null) {
            ++n;
            // convert the line into a Bonaportable
            buffer.setLength(0);
            buffer.append(line);
            buffer.append("\n");
            StringBuilderParser sbp = new StringBuilderParser(buffer, 0, buffer.length());
            BonaPortable record = sbp.readRecord();
            whereToPut.accept(record, n);
        }
    }
}
