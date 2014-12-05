package de.jpaw.bonaparte.batch8.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;

public class Bonaparte2Bytes implements Function <BonaPortable,byte []> {
    private ByteArrayComposer bac = new ByteArrayComposer();    // share this across invocations

    @Override
    public byte [] apply(BonaPortable t) {
        bac.reset();
        bac.writeRecord(t);
        return bac.getBytes();
    }

}
