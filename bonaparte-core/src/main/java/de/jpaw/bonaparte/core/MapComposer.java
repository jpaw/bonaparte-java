package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** Represents composer which does not serialize, but instead puts all objects into a Map.
 * Only the clear() and add() methods of the Map interface are used by this implementation.
 * This implementation could be used to convert the object into a structure suitable for FreeMarker or to JSON.
 * For converted objects, the PQON can be stored optionally. */
public class MapComposer extends NoOpComposer<RuntimeException> implements MessageComposer<RuntimeException> {
    final protected Map<String, Object> storage;
    final protected boolean storeNulls;
    final protected boolean storePQON;      // set to true for JSON conversions
    
    static public Map<String,Object> marshal(BonaCustom obj) {
        MapComposer mc = new MapComposer();
        obj.serializeSub(mc);
        return mc.getStorage();
    }
    
    static public Map<String,Object> toJsonMap(BonaCustom obj) {
        return marshal(obj, false, true);
    }
    
    static public Map<String,Object> marshal(BonaCustom obj, boolean storeNulls, boolean storePQON) {
        final Map<String, Object> map = new HashMap<String, Object>();
        if (storePQON)
            map.put("$PQON", obj.ret$PQON());
        MapComposer mc = new MapComposer(map, storeNulls, storePQON);
        obj.serializeSub(mc);
        return mc.getStorage();
    }
    
    /** Creates a new ListComposer for a given preallocated external storage.
     * keepObjects = true replaces the prior ListObjComposer */
    public MapComposer(final Map<String, Object> storage, boolean storeNulls, boolean storePQON) {
        this.storage = storage;
        this.storeNulls = storeNulls;
        this.storePQON = storePQON;
    }

    /** Creates a new ListComposer for a given preallocated external storage.
     * keepObjects = true replaces the prior ListObjComposer */
    public MapComposer(final Map<String, Object> storage) {
        this(storage, true, false);
    }

    /** Creates a new ListComposer, creating an own internal storage. */
    public MapComposer() {
        this(new HashMap<String, Object>(), true, false);
    }


    public Map<String, Object> getStorage() {
        return storage;
    }

    public void reset() {
        storage.clear();
    }
    
    protected void store(FieldDefinition di, Object x) {
        if (x != null || storeNulls)
            storage.put(di.getName(), x);
    }

    @Override
    public void writeNull(FieldDefinition di) {
        store(di, null);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) {
        store(di, null);
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        store(di, s);
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        store(di, Boolean.valueOf(b));
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        store(di, Character.valueOf(c));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        store(di, Double.valueOf(d));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        store(di, Float.valueOf(f));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        store(di, Byte.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        store(di, Short.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        store(di, Integer.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        store(di, Long.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) {
        store(di, n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        store(di, n);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        store(di, n);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        store(di, b);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        store(di, b);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        store(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        store(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
        store(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
        store(di, t);
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        if (obj == null) {
            writeNull(di);
        } else {
            store(di, marshal(obj, storeNulls, storePQON));
        }
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) {
        if (n == null)
            writeNull(di);
        else
            store(di, Integer.valueOf(n.ordinal())); 
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) {
        if (n == null)
            writeNull(di);
        else
            store(di, n.getToken());
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) {
        if (n == null)
            writeNull(di);
        else
            store(di, n.getToken());
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) {
        return false;
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) {
        store(di, obj);
    }

    @Override
    public void addField(ObjectReference di, Object obj) {
        store(di, obj);
    }
}
