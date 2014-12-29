package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.util.QuickConverter;
import de.jpaw.util.ByteBuilder;

public class ConverterCompactByte implements QuickConverter<byte []> {

    /** Serializes an object using the compact binary notation into byte []. */
    @Override
    public byte [] marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        ByteBuilder buff = new ByteBuilder();
        CompactByteArrayComposer composer = new CompactByteArrayComposer(buff, false);
        composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        return buff.getBytes();
    }

    /** Parses a byte array in compact format into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(byte [] data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length == 0)
            return null;
        CompactByteArrayParser parser = new CompactByteArrayParser(data, 0, -1);
        return parser.readObject(StaticMeta.OUTER_BONAPORTABLE, expectedClass);
    }
}
