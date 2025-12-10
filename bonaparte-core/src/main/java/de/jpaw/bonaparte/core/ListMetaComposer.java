package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.jpaw.bonaparte.enums.BonaByteEnumSet;
import de.jpaw.bonaparte.enums.BonaIntEnumSet;
import de.jpaw.bonaparte.enums.BonaLongEnumSet;
import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaShortEnumSet;
import de.jpaw.bonaparte.enums.BonaStringEnumSet;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.AlphanumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;

/** Represents composer which does not serialize, but instead appends all objects into a list.
 * Only the clear() and add() methods of the List interface are used by this implementation.
 * This implementation is not ideal, since it may unbox/rebox objects of the BonaPortables.
 * To improve it, the BonaCustom interface would need to be changed. */
public class ListMetaComposer extends NoOpComposer<RuntimeException> implements MessageComposer<RuntimeException> {
    final protected List<DataAndMeta> storage;
    final protected boolean doDeepCopies;
    final protected boolean keepObjects;
    final protected boolean keepExternals;
    final protected boolean convertEnums;
    final protected boolean convertEnumsets;


    /**
     * Creates a new ListMetaComposer for a given preallocated external storage.
     */
    public ListMetaComposer(final List<DataAndMeta> storage, boolean doDeepCopies, boolean keepObjects, boolean keepExternals, boolean convertEnums, boolean convertEnumsets) {
        this.storage = storage;
        this.doDeepCopies = doDeepCopies;
        this.keepObjects = keepObjects;
        this.keepExternals = keepExternals;
        this.convertEnums = convertEnums;
        this.convertEnumsets = convertEnumsets;
    }

    /** Creates a new ListMetaComposer for a given preallocated external storage.
     * keepObjects = true replaces the prior ListObjMetaComposer */
    public ListMetaComposer(final List<DataAndMeta> storage, boolean doDeepCopies, boolean keepObjects, boolean keepExternals) {
        this(storage, doDeepCopies, keepObjects, keepExternals, false, true);  // for historical reasons, enumsets are converted by default
    }

    /** Creates a new ListMetaComposer, creating an own internal storage. */
    public ListMetaComposer(boolean doDeepCopies, boolean keepObjects, boolean keepExternals) {
        this(new ArrayList<DataAndMeta>(), doDeepCopies, keepObjects, keepExternals);
    }

    /** Creates a new default ListMetaComposer, creating an own internal storage. */
    public ListMetaComposer() {
        this(new ArrayList<DataAndMeta>(), false, true, true, false, false);
    }


    protected void add(FieldDefinition di, Object o) {
        storage.add(new DataAndMeta(di, o));
    }

    public List<DataAndMeta> getStorage() {
        return storage;
    }
    public boolean getDoDeepCopies() {
        return doDeepCopies;
    }

    public void reset() {
        storage.clear();
    }

    @Override
    public void writeNull(FieldDefinition di) {
        add(di,null);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) {
        add(di,null);
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        add(di, s);
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        add(di, Boolean.valueOf(b));
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        add(di, Character.valueOf(c));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        add(di, Double.valueOf(d));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        add(di, Float.valueOf(f));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        add(di, Byte.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        add(di, Short.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        add(di, Integer.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        add(di, Long.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) {
        add(di, n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        add(di, n);
    }

    @Override
    public <F extends FixedPointBase<F>> void addField(BasicNumericElementaryDataItem di, F n) {
        add(di, n);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        add(di, n);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        add(di, b);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b == null)
            writeNull(di);
        else
            add(di, doDeepCopies ? Arrays.copyOf(b, b.length) : b);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        add(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        add(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
        add(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
        add(di, t);
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        if (obj == null) {
            writeNull(null);
        } else {
            if (keepObjects) {
                add(di, obj);
            } else {
                startObject(di, obj);
                obj.serializeSub(this);
                terminateObject(di, obj);
            }
        }
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) {
        if (convertEnums)
            add(di, n == null ? null : Integer.valueOf(n.ordinal()));
        else
            add(di, n);
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) {
        if (convertEnums)
            add(di, n == null ? null : n.getToken());
        else
            add(di, n);
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) {
        if (convertEnums)
            add(di, n == null ? null : n.getToken());
        else
            add(di, n);
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) {
        if (keepExternals) {
            add(di, obj);
        }
        return keepExternals;
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) {
        add(di, obj);
    }

    @Override
    public void addField(ObjectReference di, List<Object> obj) {
        add(di, obj);
    }

    @Override
    public void addField(ObjectReference di, Object obj) {
        add(di, obj);
    }

    // Enumsets
    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaByteEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }
    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaShortEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }
    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaIntEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }
    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaLongEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }

    @Override
    public <S extends TokenizableEnum> void addField(AlphanumericEnumSetDataItem di, BonaStringEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }

    @Override
    public <S extends TokenizableEnum> void addField(XEnumSetDataItem di, BonaStringEnumSet<S> n) {
        if (convertEnumsets)
            add(di, n == null ? null : n.getBitmap());
        else
            add(di, n);
    }
}
