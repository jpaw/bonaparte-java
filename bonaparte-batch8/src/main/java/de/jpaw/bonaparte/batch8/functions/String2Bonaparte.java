package de.jpaw.bonaparte.batch8.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringBuilderParser;

public class String2Bonaparte implements Function <String,BonaPortable> {

    @Override
    public BonaPortable apply(String t) {
        StringBuilderParser sbp = new StringBuilderParser(t, 0, t.length());
        BonaPortable record;
        try {
            record = sbp.readRecord();
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
        return record;
    }
}