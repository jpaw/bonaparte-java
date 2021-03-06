package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.util.QuickConverter;

/** Immutable class which implements the QuickConverter into/from String.
 * Every invocation will create and destroy their own Composer / Parser instance, therefore
 * a single instance of this class can be shared across multiple threads.
 */
public class ConverterBonaString implements QuickConverter<String> {

    /** Serializes an object using the "almost readable" notation into a String. */
    @Override
    public String marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(INITIAL_BUFFER_SIZE); // guess some initial size
        StringBuilderComposer composer = new StringBuilderComposer(buff);
        composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
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
