package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;

public class Compact2Bonaparte<T extends BonaPortable> implements Function <byte [],T> {
    private final CompactByteArrayParser parser = new CompactByteArrayParser(null, 0, 0);
    private final Class<T> classToExpect;

    public Compact2Bonaparte(Class<T> classToExpect) {
        this.classToExpect = classToExpect;
    }

    @Override
    public T apply(byte [] t) {
        if (t == null || t.length == 0)
            return null;
        parser.setSource(t);
        try {
            return parser.readObject(StaticMeta.OUTER_BONAPORTABLE, classToExpect);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }
}
