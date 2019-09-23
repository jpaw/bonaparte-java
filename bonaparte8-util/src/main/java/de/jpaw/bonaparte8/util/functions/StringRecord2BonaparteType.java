package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderParser;

public class StringRecord2BonaparteType<T extends BonaPortable> implements Function <String,T> {
    private final StringBuilderParser parser = new StringBuilderParser(null, 0, 0);
    private final Class<T> classToExpect;

    public StringRecord2BonaparteType(Class<T> classToExpect) {
        this.classToExpect = classToExpect;
    }

    @Override
    public T apply(String t) {
        if (t == null || t.length() == 0)
            return null;
        parser.setSource(t);
        try {
            return classToExpect.cast(parser.readRecord());
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }

}
