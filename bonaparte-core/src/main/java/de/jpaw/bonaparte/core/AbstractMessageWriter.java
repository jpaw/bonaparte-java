package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.Collection;

public abstract class AbstractMessageWriter<E extends Exception> extends Settings implements MessageWriter<E> {
    
    /** Writes a list or set of objects, with proper separators.
     * For most implementations, the default implementation should be sufficient.
     */
    @Override
    public void writeTransmission(Collection<? extends BonaCustom> coll) throws E {
        startTransmission();
        for (BonaCustom obj: coll) {
            writeRecord(obj);
        }
        terminateTransmission();
    }

    /** Writes a colection of objects via Iterable, with proper separators.
     * For most implementations, the default implementation should be sufficient.
     */
    @Override
    public void writeTransmission(Iterable<? extends BonaCustom> coll) throws E {
        startTransmission();
        for (BonaCustom obj: coll) {
            writeRecord(obj);
        }
        terminateTransmission();
    }
    
    @Override
    public void close() throws IOException {
    }
}
