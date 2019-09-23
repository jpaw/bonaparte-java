package de.jpaw.bonaparte8.util.functions;

import java.nio.charset.Charset;
import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.util.ByteBuilder;

/** Mutable class which implements a mapper from BonaPortable into byte[], using the compact format.
 * The composer instance will live across invocations, reducing GC overhead,
 * but with the drawback that the instance is not thread-safe.
 *
 */
public class Bonaparte2Compact implements Function <BonaPortable,byte []> {
    private final ByteBuilder buff;
    private final CompactByteArrayComposer composer;
    private final boolean writeRecords;

    /** Constructs a composer which creates objects or records, using a specified charset (or the default UTF-8, in case the charset parameter is null). */
    public Bonaparte2Compact(boolean writeRecords, Charset charset) {
        this.writeRecords = writeRecords;
        this.buff = new ByteBuilder(8000, charset);
        this.composer = new CompactByteArrayComposer(buff, false);    // share this across invocations
    }

    /** Constructs a composer which creates objects, using the default charset. */
    public Bonaparte2Compact() {
        this(false, null);
    }

    /** Constructs a composer which creates records, using the default charset. Parameter provided for consistency, but is ignored. */
    public Bonaparte2Compact(Boolean writeCRs) {
        this(true, null);
    }

    /** Serializes an object using the compact format byte []. */
    @Override
    public byte [] apply(BonaPortable obj) {
        if (obj == null)
            return null;
        buff.setLength(0);
        composer.reset();
        if (writeRecords)
            composer.writeRecord(obj);
        else
            composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        return buff.getBytes();
    }
}
