package de.jpaw.bonaparte.batch.endpoints;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterFile;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;

/** This one converts directly to byte [], and currently is hardcoded to UTF-8.
 * It avoids some copying of data but may use more system calls. */
public class BatchWriterBonaparte2File extends BatchWriterFile implements BatchWriter<BonaPortable> {
    private ByteArrayComposer bac = new ByteArrayComposer();    // share this across invocations

    @Override
    public void accept(int no, BonaPortable response) throws Exception {
        bac.reset();
        bac.writeRecord(response);
        uncompressedStream.write(bac.getBuffer(), 0, bac.getLength());
        bac.reset();
        if (delayInMillis > 0)
            Thread.sleep(delayInMillis);
    }
}
