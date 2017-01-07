package de.jpaw.bonaparte.core;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Composer intended for IMDGs.
 * Goals:
 * 1) be as efficient as reasonable, to save memory (= allows to cache more objects in same amount of RAM = more speed)
 * 2) don't allocate temporary objects during serialization (avoid GC overhead), unless absolutely required
 *
 * @author Michael Bischoff
 *
 */
public class CompactComposer extends AbstractCompactComposer {

    // entry called from generated objects: (Object header has been written already by internal methods (and unfortunately in some different fashion...))
    public static void serialize(BonaCustom obj, DataOutput _out, boolean recommendIdentifiable) throws IOException {
        MessageComposer<IOException> _w = new CompactComposer(_out, recommendIdentifiable);
        obj.serializeSub(_w);
        _w.terminateObject(StaticMeta.OUTER_BONAPORTABLE, obj);
    }

    public CompactComposer(DataOutput out, boolean recommendIdentifiable) {
        this(out, ObjectReuseStrategy.defaultStrategy, recommendIdentifiable);
    }

    /**
     * Creates a new ByteArrayComposer, using this classes static default
     * Charset
     **/
    public CompactComposer(DataOutput out, ObjectReuseStrategy reuseStrategy, boolean recommendIdentifiable) {
        super(out, reuseStrategy, recommendIdentifiable, true);
    }

    /**
     * Creates a new ByteArrayComposer, using this classes static default
     * Charset
     **/
    public CompactComposer(DataOutput out, ObjectReuseStrategy reuseStrategy, boolean recommendIdentifiable, boolean useJsonForBonaCustomInElements) {
        super(out, reuseStrategy, recommendIdentifiable, useJsonForBonaCustomInElements);
    }
}
