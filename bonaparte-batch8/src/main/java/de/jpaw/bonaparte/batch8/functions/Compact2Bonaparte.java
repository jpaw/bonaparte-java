package de.jpaw.bonaparte.batch8.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class Compact2Bonaparte implements Function <byte [],BonaPortable> {

    @Override
    public BonaPortable apply(byte [] t) {
        CompactByteArrayParser cbap = new CompactByteArrayParser(t, 0, t.length);
        BonaPortable record;
        try {
            record = cbap.readRecord();
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
        return record;
    }
}