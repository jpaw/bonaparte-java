package de.jpaw.bonaparte.util.impl;

import java.nio.charset.Charset;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.util.QuickConverter;

/** Immutable class which implements the QuickConverter into/from byte[].
 * Every invocation will create and destroy their own Composer / Parser instance, therefore
 * a single instance of this class can be shared across multiple threads.
 */
public class RecordConverterBonaByte implements QuickConverter<byte []> {
    private final Boolean writeCRs;
    private final Charset charset;

    // another good example where default parameters would be great to have!

    /** Creates a record converter for default settings. */
    public RecordConverterBonaByte() {
        this.writeCRs = null;
        this.charset = null;
    }

    /** Creates a record converter with the option to configure portable CR/LF settings. */
    public RecordConverterBonaByte(Boolean writeCRs) {
        this.writeCRs = writeCRs;
        this.charset = null;
    }

    /** Creates a record converter with the option to configure a specific Charset. */
    public RecordConverterBonaByte(Charset charset) {
        this.writeCRs = null;
        this.charset = charset;
    }

    /** Creates a record converter with the option to configure a specific Charset. */
    public RecordConverterBonaByte(Boolean writeCRs, Charset charset) {
        this.writeCRs = writeCRs;
        this.charset = charset;
    }



    /** Serializes an object using the "almost readable" notation into byte [], including record terminators. */
    @Override
    public byte [] marshal(BonaPortable obj) {
        if (obj == null)
            return null;
        ByteArrayComposer composer = new ByteArrayComposer();
        if (writeCRs != null)
            composer.setWriteCRs(writeCRs);
        if (charset != null)
            composer.setCharset(charset);
        composer.writeRecord(obj);
        return composer.getBytes();
    }

    /** Parses a byte array in "almost readable" format into a specific BonaPortable. */
    @Override
    public <T extends BonaPortable> T unmarshal(byte [] data, Class<T> expectedClass) throws MessageParserException {
        if (data == null || data.length == 0)
            return null;
        ByteArrayParser parser = new ByteArrayParser(data, 0, -1);
        if (charset != null)
            parser.setCharset(charset);
        return expectedClass.cast(parser.readRecord());
    }
}
