package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class CompactRecord2Bonaparte implements Function <byte [],BonaPortable> {
    private final CompactByteArrayParser parser = new CompactByteArrayParser(null, 0, 0);

    @Override
    public BonaPortable apply(byte [] t) {
        if (t == null || t.length == 0)
            return null;
        parser.setSource(t);
        try {
            return parser.readRecord();
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }
}
