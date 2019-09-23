package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderParser;

public class StringRecord2Bonaparte implements Function <String,BonaPortable> {
    private final StringBuilderParser parser = new StringBuilderParser(null, 0, 0);

    @Override
    public BonaPortable apply(String t) {
        if (t == null || t.length() == 0)
            return null;
        parser.setSource(t);
        try {
            return parser.readRecord();
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }
}
