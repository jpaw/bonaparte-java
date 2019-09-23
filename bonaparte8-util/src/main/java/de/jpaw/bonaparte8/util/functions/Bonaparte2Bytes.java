package de.jpaw.bonaparte8.util.functions;

import java.nio.charset.Charset;
import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.StaticMeta;

/** Mutable class which implements a mapper from BonaPortable into byte[].
 * The composer instance will live across invocations, reducing GC overhead,
 * but with the drawback that the instance is not thread-safe.
 *
 * The line ending behaviour can be set via constructor.
 * The charset can be changed.
 */
public class Bonaparte2Bytes implements Function <BonaPortable,byte []> {
    private final ByteArrayComposer composer = new ByteArrayComposer();    // share this across invocations
    private final boolean writeRecords;

    /** Constructs a composer which creates objects. */
    public Bonaparte2Bytes() {
        writeRecords = false;
    }

    /** Constructs a composer which creates records.
     * The line ending behaviour is operating system dependent if the parameter is null,
     * or defined as per parameter. */
    public Bonaparte2Bytes(Boolean writeCRs) {
        writeRecords = true;
        if (writeCRs != null)
            composer.setWriteCRs(writeRecords);
    }

    /** Specifies the charset for the composer. Returns this to allow fluent coding. */
    public Bonaparte2Bytes setCharset(Charset charset) {
        composer.setCharset(charset);
        return this;
    }

    /** Serializes an object using the "almost readable" notation into byte []. */
    @Override
    public byte [] apply(BonaPortable obj) {
        if (obj == null)
            return null;
        composer.reset();
        if (writeRecords)
            composer.writeRecord(obj);
        else
            composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        return composer.getBytes();
    }

}
