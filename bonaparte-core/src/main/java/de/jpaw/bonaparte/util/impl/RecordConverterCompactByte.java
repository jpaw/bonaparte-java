package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.util.QuickConverter;
import de.jpaw.util.ByteBuilder;

/** Immutable class which implements the QuickConverter into/from byte[] using the compact format.
 * Every invocation will create and destroy their own Composer / Parser instance, therefore
 * a single instance of this class can be shared across multiple threads.
 */
public class RecordConverterCompactByte implements QuickConverter<byte []> {

    /** Serializes an object using the compact binary notation into byte [], including record terminators. */
    @Override
    public byte [] marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        ByteBuilder buff = new ByteBuilder();
        CompactByteArrayComposer composer = new CompactByteArrayComposer(buff, false);
        composer.writeRecord(obj);
        return buff.getBytes();
    }

    /** Parses a byte array in compact format into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(byte [] data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length == 0)
            return null;
        CompactByteArrayParser parser = new CompactByteArrayParser(data, 0, -1);
        return expectedClass.cast(parser.readRecord());
    }
}
