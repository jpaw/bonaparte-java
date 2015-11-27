package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

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
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.ByteArray;
import de.jpaw.util.MapIterator;

// this parser will accept a subset of all possible convertable input types
// the following types can be assumed to be accepted:
// - the desired type itself
// - a string
// - the possible types created by the JsonParser for input originating from the corresponding type

public class MapParser extends AbstractMessageParser<MessageParserException> implements MessageParser<MessageParserException> {

    private final Map<String, Object> map;
    private Iterator<Object> iter = null;
    
    private String currentClass = "N/A";
    protected final boolean instantInMillis;
    
    protected final StringParserUtil spu;
    
    protected MessageParserException err(int errno, FieldDefinition di) {
        return new MessageParserException(errno, di.getName(), -1, currentClass);
    }
    
    public MapParser(Map<String, Object> map, boolean instantInMillis) {
        this.map = map;
        // currentClass = map.get("$PQON");
        this.instantInMillis = instantInMillis;
        this.spu = new StringParserUtil(new ParsePositionProvider() {

            @Override
            public int getParsePosition() {
                return -1;
            }

            @Override
            public String getCurrentClassName() {
                return currentClass;
            }
        }, instantInMillis);
    }
    
    // populate an existing object from a provided map
    public static void populateFrom(BonaPortable obj, Map<String, Object> map) throws MessageParserException {
        obj.deserialize(new MapParser(map, false));
    }
    
    private static BonaPortable allocObject(Map<String, Object> map, ObjectReference di) throws MessageParserException {
        Object pqon = map.get("$PQON");
        if (pqon == null || !(pqon instanceof String)) {
            // fallback: use the lower bound of di, if provided
            if (di.getLowerBound() == null)
                throw new MessageParserException(MessageParserException.JSON_NO_PQON);
            pqon = di.getLowerBound().getName();
        }
        return BonaPortableFactory.createObject((String)pqon);
    }
    
    // convert a map to BonaPortable, read class info from the Map ($PQON entries)
    public static BonaPortable asBonaPortable(Map<String, Object> map, ObjectReference di) throws MessageParserException {
        BonaPortable obj = allocObject(map, di);
        populateFrom(obj, map);
        return obj;
    }
    
    private Object getNext(FieldDefinition di) {
        return (iter != null) ? iter.next() : map.get(di.getName());
    }
    
    private Object get(FieldDefinition di) throws MessageParserException {
        Object z = getNext(di);
        if (z == null && di.getIsRequired())
            throw err(MessageParserException.ILLEGAL_EXPLICIT_NULL, di);
        return z;
    }
    
    @Override
    public MessageParserException enumExceptionConverter(IllegalArgumentException e) throws MessageParserException {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage(), -1, currentClass);
    }

    @Override
    public MessageParserException customExceptionConverter(String msg, Exception e) throws MessageParserException {
        return new MessageParserException(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg, -1, currentClass);
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Character)
            return (Character)z;
        // must be string of length 1 otherwise
        if (z instanceof String) {
            return spu.readCharacter(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof UUID)
            return (UUID)z;
        if (z instanceof String) {
            return spu.readUUID(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Boolean)
            return (Boolean)z;
        if (z instanceof String) {
            return spu.readBoolean(di, (String)z);
        }
        if (z instanceof Number) {
            return ((Number)z).doubleValue() == 0.0 ? Boolean.TRUE : Boolean.FALSE;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Double.valueOf(((Number)z).doubleValue());
        }
        if (z instanceof String) {
            return Double.valueOf(spu.readPrimitiveDouble(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Float.valueOf(((Number)z).floatValue());
        }
        if (z instanceof String) {
            return Float.valueOf(spu.readPrimitiveFloat(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Long.valueOf(((Number)z).longValue());
        }
        if (z instanceof String) {
            return Long.valueOf(spu.readPrimitiveLong(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Integer.valueOf(((Number)z).intValue());
        }
        if (z instanceof String) {
            return Integer.valueOf(spu.readPrimitiveInteger(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Short.valueOf(((Number)z).shortValue());
        }
        if (z instanceof String) {
            return Short.valueOf(spu.readPrimitiveShort(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Byte.valueOf(((Number)z).byteValue());
        }
        if (z instanceof String) {
            return Byte.valueOf(spu.readPrimitiveByte(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof BigInteger)       // todo: check scale?
            return (BigInteger)z;
        if (z instanceof Number) {
            // check int, long
            if (z instanceof Integer)
                return BigInteger.valueOf(((Integer)z).intValue());
            if (z instanceof Long)
                return BigInteger.valueOf(((Long)z).longValue());
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        if (z instanceof String) {
            return spu.readBigInteger(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }
    
    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof BigDecimal)       // todo: check scale?
            return (BigDecimal)z;
        if (z instanceof Number) {
            // check double, int, long
            if (z instanceof Integer)
                return BigDecimal.valueOf(((Integer)z).intValue());
            if (z instanceof Long)
                return BigDecimal.valueOf(((Long)z).longValue());
            if (z instanceof Double)
                return BigDecimal.valueOf(((Double)z).doubleValue());
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        if (z instanceof String) {
            return spu.readBigDecimal(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String) {
            return spu.readAscii(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String) {
            return spu.readString(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof ByteArray) {
            final ByteArray zz = (ByteArray)z;
            if (zz.length() > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz;
        }
        if (z instanceof byte []) {
            final byte [] zz = (byte [])z;
            if (zz.length > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz.length == 0 ? ByteArray.ZERO_BYTE_ARRAY : new ByteArray(zz);
        }
        if (z instanceof String) {
            return spu.readByteArray(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof ByteArray) {
            final ByteArray zz = (ByteArray)z;
            if (zz.length() > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz.getBytes();
        }
        if (z instanceof byte []) {
            final byte [] zz = (byte [])z;
            if (zz.length > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz;
        }
        if (z instanceof String) {
            return spu.readRaw(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Instant) {
            return (Instant)z;
        }
        if (z instanceof String) {
            return spu.readInstant(di, (String)z);      // assumes precision = 1 second, with fractionals if ms
        }
        if (z instanceof Number) {
            // convert number of seconds to Instant
            return new Instant((long)(((Number)z).doubleValue() * 1000.0));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                return LocalDate.parse((String)z, ISODateTimeFormat.date());
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalDate) {
            return (LocalDate)z;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                return LocalTime.parse((String)z, di.getFractionalSeconds() > 0 ? ISODateTimeFormat.time() : ISODateTimeFormat.timeNoMillis());
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalTime) {
            return (LocalTime)z;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                return LocalDateTime.parse((String)z, di.getFractionalSeconds() > 0 ? ISODateTimeFormat.dateTime() : ISODateTimeFormat.dateTimeNoMillis());
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalDateTime) {
            return (LocalDateTime)z;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public <R extends BonaPortable> R readObject(ObjectReference di, Class<R> type) throws MessageParserException {
        // read of a nested object => uses a separate map!
        Object z = get(di);
        if (z == null)
            return null;
        
        BonaCustom obj;
        if (z instanceof Map<?,?>) {
             obj = asBonaPortable((Map<String, Object>)z, di);      // recursive invocation
        } else if (z instanceof BonaCustom) {
            obj = (BonaCustom)z;
        } else {
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        
        if (obj.getClass().equals(type))
            return (R)obj;
        if (!di.getAllowSubclasses() && !type.isAssignableFrom(obj.getClass()))
            throw new MessageParserException(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s or subclass for %s)",
                     obj.getClass().getSimpleName(), type.getSimpleName(), di.getName()), -1, currentClass);
        return (R)obj;
    }

    @Override
    public Map<String, Object> readJson(ObjectReference di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Map<?,?>) {
            return (Map<String, Object>)z;      // no check possible due to Java type erasure
        }
        // reverse mapping: BonaPortable => Map<>. It would be weird to encounter here, but hey....
        if (z instanceof BonaCustom) {
            return MapComposer.marshal((BonaCustom)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public List<Object> readArray(ObjectReference di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof List<?>) {
            return (List<Object>)z;      // no check possible due to Java type erasure
        }
        if (z instanceof Object []) {
            return new ArrayList<Object>(Arrays.asList((Object [])z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }
    
    @Override
    public Object readElement(ObjectReference di) throws MessageParserException {
        return get(di);
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        if (iter != null)
            throw new RuntimeException("Nested collection should not happen, but occurred for Map " + currentClass + "." + di.getName());
        Object z = getNext(di);
        if (z == null) {
            if (di.getIsAggregateRequired())
                throw err(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, di);
            return -1;
        }
        if (z instanceof Map<?,?>) {
            Map<Object,Object> m = (Map<Object,Object>)z;
            iter = new MapIterator<Object>(m);
            return m.size();
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws MessageParserException {
        if (iter != null)
            throw new RuntimeException("Nested collection should not happen, but occurred for Collection " + currentClass + "." + di.getName());
        Object z = getNext(di);
        if (z == null) {
            if (di.getIsAggregateRequired())
                throw err(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, di);
            return -1;
        }
        if (z instanceof List<?>) {
            List<Object> l = (List<Object>)z;
            iter = l.iterator();
            return l.size();
        }
        if (z instanceof Set<?>) {
            Set<Object> s = (Set<Object>)z;
            iter = s.iterator();
            return s.size();
        }
        if (z instanceof Object []) {
            Object [] a = (Object [])z;
            iter = Arrays.asList(a).iterator();
            return a.length;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
        if (iter == null)
            throw new RuntimeException("Cannot end collection when none has been started.");
        if (iter.hasNext())
            throw new RuntimeException("Should not end collection when iterator has not been exhausted.");
        iter = null;
    }

    @Override
    public BonaPortable readRecord() throws MessageParserException {
        BonaPortable obj = allocObject(map, null);
        obj.deserialize(this);
        return obj;
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
        Object z = getNext(di);
        if (z == null) {
            T result = factory.getNullToken();
            if (result == null && di.getIsRequired())
                throw err(MessageParserException.EMPTY_BUT_REQUIRED_FIELD, di);
            return result;
        }
        if (z instanceof String) {
            final T value = factory.getByToken((String)z);
            if (value == null)
                throw err(MessageParserException.INVALID_ENUM_TOKEN, di);
            return value;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    // special handling of enums: access the original name, not the one with $token suffix: take care when di and when edi is used!
    @Override
    public Integer readEnum(EnumDataItem edi, BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(edi);
        if (z == null)
            return null;
        if (z instanceof Number) {
            return Integer.valueOf(((Number)z).intValue());
        }
        if (z instanceof String) {
            return Integer.valueOf(spu.readPrimitiveInteger(di, (String)z));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, edi);
    }

    // special handling of enums: access the original name, not the one with $token suffix
    @Override
    public String readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(edi);
        if (z == null)
            return null;
        if (z instanceof String) {
            return spu.readString(di, (String)z);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, edi);
    }
}
