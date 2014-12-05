package de.jpaw.bonaparte.batch8.functions;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.util.ByteBuilder;

public class Bonaparte2Compact implements Function <BonaPortable,byte []> {
    ByteBuilder buff = new ByteBuilder(8000, StandardCharsets.UTF_8);
    private CompactByteArrayComposer bac = new CompactByteArrayComposer(buff, false);    // share this across invocations

    @Override
    public byte [] apply(BonaPortable t) {
        buff.setLength(0);
        bac.reset();
        bac.writeRecord(t);
        return buff.getBytes();
    }
}
