package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/**
 * Composer intended for IMDGs.
 * Goals:
 * 1) be as efficient as reasonable, to save memory (= allows to cache more objects in same amount of RAM = more speed)
 * 2) don't allocate temporary objects during serialization (avoid GC overhead), unless absolutely required
 *
 * @author Michael Bischoff
 *
 */
public class CompactByteArrayComposer extends AbstractCompactComposer implements BufferedMessageComposer<IOException> {

    private static final int DEFAULT_BUFFER_SIZE = 8000;
    protected final ByteBuilder out;


    /** Quick conversion utility method, for use by code generators. (null safe).   Treats embedded JSONs differently than marshal().  */
    public static byte [] marshal(ObjectReference di, BonaPortable x, boolean useJsonForBonaCustomInElements) {
        if (x == null)
            return null;
        final ByteBuilder b = new ByteBuilder();
        new CompactByteArrayComposer(b, ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements).addField(di, x);
        return b.getBytes();
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static byte [] marshal(ObjectReference di, BonaPortable x) {
        return marshal(di, x, true);
    }

    /** Quick conversion utility method, for use by code generators. (null safe, avoids double copying of the result) */
    public static ByteArray marshalAsByteArray(ObjectReference di, BonaPortable x, boolean useJsonForBonaCustomInElements) {
        if (x == null)
            return null; // consistent with the other methods: f(null) = null  //  ByteArray.ZERO_BYTE_ARRAY;
        ByteBuilder b = new ByteBuilder();
        new CompactByteArrayComposer(b, ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements).addField(di, x);
        return new ByteArray(b.getCurrentBuffer(), 0, b.length());
    }
    public static ByteArray marshalAsByteArray(ObjectReference di, BonaPortable x) {
        return marshalAsByteArray(di, x, true);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static byte [] marshalAsElement(ObjectReference di, Object x, boolean useJsonForBonaCustomInElements) {
        if (x == null)
            return null;
        ByteBuilder b = new ByteBuilder();
        try {
            new CompactByteArrayComposer(b, ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements).addField(di, x);
        } catch (IOException e) {
            // NOT POSSIBLE
            throw new RuntimeException(e);
        }
        return b.getBytes();
    }

    public static byte [] marshalAsElement(ObjectReference di, Object x) {
        return marshalAsElement(di, x, true);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static byte [] marshalAsArray(ObjectReference di, List<Object> x, boolean useJsonForBonaCustomInElements) {
        if (x == null)
            return null;
        ByteBuilder b = new ByteBuilder();
        try {
            new CompactByteArrayComposer(b, ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements).addField(di, x);
        } catch (IOException e) {
            // NOT POSSIBLE
            throw new RuntimeException(e);
        }
        return b.getBytes();
    }
    public static byte [] marshalAsArray(ObjectReference di, List<Object> x) {
        return marshalAsArray(di, x, true);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static byte [] marshalAsJson(ObjectReference di, Map<String, Object> x, boolean useJsonForBonaCustomInElements) {
        if (x == null)
            return null;
        ByteBuilder b = new ByteBuilder();
        try {
            new CompactByteArrayComposer(b, ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements).addField(di, x);
        } catch (IOException e) {
            // NOT POSSIBLE
            throw new RuntimeException(e);
        }
        return b.getBytes();
    }
    public static byte [] marshalAsJson(ObjectReference di, Map<String, Object> x) {
        return marshalAsJson(di, x, true);
    }


    public CompactByteArrayComposer() {
        this(new ByteBuilder(DEFAULT_BUFFER_SIZE, getDefaultCharset()), ObjectReuseStrategy.defaultStrategy, false, true);
    }

    public CompactByteArrayComposer(boolean useJsonForBonaCustomInElements) {
        this(new ByteBuilder(DEFAULT_BUFFER_SIZE, getDefaultCharset()), ObjectReuseStrategy.defaultStrategy, false, useJsonForBonaCustomInElements);
    }

    public CompactByteArrayComposer(int bufferSize, boolean recommendIdentifiable) {
        this(new ByteBuilder(bufferSize, getDefaultCharset()), ObjectReuseStrategy.defaultStrategy, recommendIdentifiable, true);
    }

    public CompactByteArrayComposer(ByteBuilder out, boolean recommendIdentifiable) {
        this(out, ObjectReuseStrategy.defaultStrategy, recommendIdentifiable, true);
    }

    /**
     * Creates a new ByteArrayComposer, using this classes static default
     * Charset
     **/
    public CompactByteArrayComposer(ByteBuilder out, ObjectReuseStrategy reuseStrategy, boolean recommendIdentifiable) {
        super(out, reuseStrategy, recommendIdentifiable, true);
        this.out = out;
    }

    /**
     * Creates a new ByteArrayComposer, using this classes static default
     * Charset
     **/
    public CompactByteArrayComposer(ByteBuilder out, ObjectReuseStrategy reuseStrategy, boolean recommendIdentifiable, boolean useJsonForBonaCustomInElements) {
        super(out, reuseStrategy, recommendIdentifiable, useJsonForBonaCustomInElements);
        this.out = out;
    }

    // must be overridden / called if caching / reuse is active!
    @Override
    public void reset() {
        super.reset();
        out.setLength(0);
    }

    public ByteBuilder getBuilder() {
        return out;
    }

    @Override
    public byte [] getBuffer() {
        return out.getCurrentBuffer();
    }

    @Override
    public int getLength() {
        return out.length();
    }

    @Override
    public byte [] getBytes() {
        return out.getBytes();
    }

    // overwrite some methods in order to eat the checked IOException
    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        try {
            super.addField(di, obj);
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeRecord(BonaCustom o) {
        try {
            super.writeRecord(o);
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeObject(BonaCustom o) {
        try {
            super.writeObject(o);
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTransmission(Iterable<? extends BonaCustom> coll) {
        try {
            super.writeTransmission(coll);
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTransmission(Collection<? extends BonaCustom> coll) {
        try {
            super.writeTransmission(coll);
        } catch (IOException e) {
            // IOException from ByteArray operation???
            throw new RuntimeException(e);
        }
    }
}
