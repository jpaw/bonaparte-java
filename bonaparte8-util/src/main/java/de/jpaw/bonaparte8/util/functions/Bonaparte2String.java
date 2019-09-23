package de.jpaw.bonaparte8.util.functions;

import java.util.function.Function;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringBuilderComposer;

/** Mutable class which implements a mapper from BonaPortable into String.
 * The composer instance will live across invocations, reducing GC overhead,
 * but with the drawback that the instance is not thread-safe.
 *
 * The line ending behaviour can be set via constructor. This also switches the mode between object creation and record creation.
 */
public class Bonaparte2String implements Function <BonaPortable,String> {
    private final StringBuilder buff = new StringBuilder(8000);
    private final StringBuilderComposer composer = new StringBuilderComposer(buff);    // share this across invocations
    private final boolean writeRecords;

    /** Constructs a composer which creates objects. */
    public Bonaparte2String() {
        writeRecords = false;
    }

    /** Constructs a composer which creates records.
     * The line ending behaviour is operating system dependent if the parameter is null,
     * or defined as per parameter. */
    public Bonaparte2String(Boolean writeCRs) {
        writeRecords = true;
        if (writeCRs != null)
            composer.setWriteCRs(writeRecords);
    }

    /** Serializes an object using the "almost readable" notation into String. */
    @Override
    public String apply(BonaPortable obj) {
        if (obj == null)
            return null;
        buff.setLength(0);
        composer.reset();
        if (writeRecords)
            composer.writeRecord(obj);
        else
            composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
        return buff.toString();
    }

}
