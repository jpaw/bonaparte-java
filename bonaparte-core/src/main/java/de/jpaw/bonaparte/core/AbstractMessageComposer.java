package de.jpaw.bonaparte.core;

import java.util.Collection;

/** Parent class of all MessageComposers. */
public abstract class AbstractMessageComposer<E extends Exception> extends Settings implements MessageComposer<E> {
    
    /** Separates two records.
     * Normally no activity is required. */
    @Override
    public void writeRecordSeparator() throws E {
    }

    /** Writes a list or set of objects, with proper separators.
     * For most implementations, the default implementation should be sufficient.
     */
    @Override
    public void writeTransmission(Collection<? extends BonaCustom> coll) throws E {
        boolean initial = true;
        startTransmission();
        for (BonaCustom obj: coll) {
            if (initial)
                initial = false;
            else
                writeRecordSeparator();
            writeRecord(obj);
        }
        terminateTransmission();
    }

    /** Writes a colection of objects via Iterable, with proper separators.
     * For most implementations, the default implementation should be sufficient.
     */
    @Override
    public void writeTransmission(Iterable<? extends BonaCustom> coll) throws E {
        boolean initial = true;
        startTransmission();
        for (BonaCustom obj: coll) {
            if (initial)
                initial = false;
            else
                writeRecordSeparator();
            writeRecord(obj);
        }
        terminateTransmission();
    }
}
