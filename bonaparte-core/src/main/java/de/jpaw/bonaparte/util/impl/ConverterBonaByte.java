package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.util.QuickConverter;

public class ConverterBonaByte implements QuickConverter<byte []> {
    
    /** Serializes an object using the "almost readable" notation into byte []. */
    @Override
    public byte [] marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        ByteArrayComposer composer = new ByteArrayComposer();
        composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        return composer.getBytes();
    }

    /** Parses a byte array in "almost readable" format into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(byte [] data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length == 0)
            return null;
        ByteArrayParser parser = new ByteArrayParser(data, 0, -1);
        return parser.readObject(StaticMeta.OUTER_BONAPORTABLE, expectedClass);
    }
}
