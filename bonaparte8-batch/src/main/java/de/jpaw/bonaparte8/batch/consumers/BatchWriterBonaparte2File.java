package de.jpaw.bonaparte8.batch.consumers;

import java.io.IOException;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw8.batch.consumers.impl.BatchWriterFile;

/** This one converts directly to byte [], and currently is hardcoded to UTF-8.
 * It avoids some copying of data but may use more system calls. */
public class BatchWriterBonaparte2File extends BatchWriterFile<BonaPortable> {
    private ByteArrayComposer bac = new ByteArrayComposer();    // share this across invocations

    @Override
    public void store(BonaPortable response, int no) {
        bac.reset();
        bac.writeRecord(response);
        try {
            uncompressedStream.write(bac.getBuffer(), 0, bac.getLength());
        } catch (IOException e) {
            throw new RuntimeException(e);      // checked exceptions don't work well with lambdas
        }
        bac.reset();
    }
}
