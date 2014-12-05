package de.jpaw.bonaparte.batch8.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StringBuilderComposer;

public class Bonaparte2String implements Function <BonaPortable,String> {
    private final StringBuilder buff = new StringBuilder(8000);
    private final StringBuilderComposer sbc = new StringBuilderComposer(buff);    // share this across invocations

    @Override
    public String apply(BonaPortable t) {
        sbc.reset();
        sbc.writeRecord(t);
        return sbc.toString();
    }

}
