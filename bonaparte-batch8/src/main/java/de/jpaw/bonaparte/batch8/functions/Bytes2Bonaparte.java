package de.jpaw.bonaparte.batch8.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class Bytes2Bonaparte implements Function <byte [],BonaPortable> {

    @Override
    public BonaPortable apply(byte [] t) {
        ByteArrayParser bap = new ByteArrayParser(t, 0, t.length);
        BonaPortable record;
        try {
            record = bap.readRecord();
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
        return record;
    }
}