package de.jpaw.bonaparte.util.impl;

import java.io.IOException;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.util.QuickConverter;

public class ConverterBonaString implements QuickConverter<String> {

    /** Serializes an object using the "almost readable" notation into a String. */
    @Override
    public String marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(INITIAL_BUFFER_SIZE); // guess some initial size
        StringBuilderComposer composer = new StringBuilderComposer(buff);
        try {
            composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        } catch (IOException e) {
            // IOException writing to a String? Java, you're not serious!
            throw new RuntimeException("Got an IOException from within StringBuilder, which should not happen, really!", e);
        }
        return buff.toString();
    }

    /** Parses a String into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(String data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length() == 0)
            return null;
        StringBuilderParser parser = new StringBuilderParser(data, 0, -1);
        return parser.readObject(StaticMeta.OUTER_BONAPORTABLE, expectedClass);
    }
}
