package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class CompactRecord2BonaparteType<T extends BonaPortable> implements Function <byte [],T> {
    private final CompactByteArrayParser parser = new CompactByteArrayParser(null, 0, 0);
    private final Class<T> classToExpect;

    public CompactRecord2BonaparteType(Class<T> classToExpect) {
        this.classToExpect = classToExpect;
    }

    @Override
    public T apply(byte [] t) {
        if (t == null || t.length == 0)
            return null;
        parser.setSource(t);
        try {
            return classToExpect.cast(parser.readRecord());
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }
}
