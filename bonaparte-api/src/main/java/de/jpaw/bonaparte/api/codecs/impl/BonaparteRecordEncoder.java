package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class BonaparteRecordEncoder<O extends BonaPortable> implements IMessageEncoder<O, byte []> {
    private final ByteArrayComposer bac = new ByteArrayComposer();

    @Override
    public byte[] encode(O obj, ObjectReference di) {
        bac.reset();
        bac.writeRecord(obj);
        return bac.getBytes();
    }
}
