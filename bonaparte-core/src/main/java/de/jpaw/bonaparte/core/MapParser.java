package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.LongFunction;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.AlphanumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDataItem;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;
import de.jpaw.util.MapIterator;

// this parser will accept a subset of all possible convertable input types
// the following types can be assumed to be accepted:
// - the desired type itself
// - a string
// - the possible types created by the JsonParser for input originating from the corresponding type

public class MapParser extends AbstractMessageParser<MessageParserException> implements MessageParser<MessageParserException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapParser.class);

    private final Map<String, Object> map;
    private final boolean readEnumOrdinals;    // false: use name, true: return ordinal for non tokenizable enums
    private final boolean readEnumTokens;      // false: use name, true: return token for tokenizable enums / xenums
    private Iterator<Object> iter = null;

    private String currentClass = "N/A";
    protected final boolean instantInMillis;

    protected final StringParserUtil spu;

    protected MessageParserException err(int errno, FieldDefinition di) {
        return new MessageParserException(errno, di.getName(), -1, currentClass);
    }

    public MapParser(Map<String, Object> map, boolean instantInMillis) {
        this(map, instantInMillis, true, true);
    }

    public MapParser(Map<String, Object> map, boolean instantInMillis, boolean readEnumOrdinals, boolean readEnumTokens) {
        this.map = map;
        // currentClass = map.get(MimeTypes.JSON_FIELD_PQON);
        this.instantInMillis  = instantInMillis;
        this.readEnumOrdinals = readEnumOrdinals;
        this.readEnumTokens   = readEnumTokens;
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

    public static BonaPortable allocObject(Map<String, Object> map, ObjectReference di) throws MessageParserException {
        final ClassDefinition lowerBound = di.getLowerBound();

        // create the object. Determine the type by the object reference, if that defines no subclassing
        // otherwise, check for special fields like $PQON (partially qualified name), and @type (fully qualified name)
        if (di.getAllowSubclasses() == false) {
            // no parameter required. determine by reference
            if (lowerBound == null)
                throw new MessageParserException(MessageParserException.JSON_BAD_OBJECTREF);
            return BonaPortableFactory.createObject(di.getLowerBound().getName());        // OK
        }

        // variable contents. see if we got a partially qualified name
        Object pqon1 = map.get(MimeTypes.JSON_FIELD_PQON);
        if (pqon1 != null && pqon1 instanceof String s1) {
            // lucky day, we got the partially qualified name
            return BonaPortableFactory.createObject(s1);
        }

        Object pqon2 = map.get(MimeTypes.JSON_FIELD_FQON);
        if (pqon2 != null && pqon2 instanceof String s2) {
            // also lucky, we got a fully qualified name
            return BonaPortableFactory.createObjectByFqon(s2);
        }

        // fallback: use the lower bound of di, if provided
        if (lowerBound == null) {
            // also no lower bound? Cannot work around that!
            throw new MessageParserException(MessageParserException.JSON_NO_PQON, di.getName(), -1, null);
        }

        if (LOGGER.isTraceEnabled()) {
            // output the map we got
            LOGGER.trace("Cannot convert Map to BonaPortable, Map dump is");
            for (Map.Entry<String, Object> me : map.entrySet())
                LOGGER.trace("    \"{}\": {}", me.getKey(), me.getValue() == null ? "null" : me.getValue().toString());
        }

        // severe issue if the base class is abstract
        if (lowerBound.getIsAbstract()) {
            LOGGER.warn("Parsed object cannot be determined, no type information provided and base object is abstract. {}: ({}...)", di.getName(), lowerBound.getName());
            throw new MessageParserException(MessageParserException.JSON_NO_PQON, di.getName(), -1, null);
        }
        // issue a warning, at least, and use the base class
        LOGGER.warn("Parsed object cannot be determined uniquely, no type information provided and subclasses allowed for {}: ({}...)", di.getName(), lowerBound.getName());
        return BonaPortableFactory.createObject(lowerBound.getName());
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
        if (z instanceof Character c)
            return c;
        // must be string of length 1 otherwise
        if (z instanceof String s) {
            return spu.readCharacter(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof UUID u)
            return u;
        if (z instanceof String s) {
            return spu.readUUID(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Boolean b)
            return b;
        if (z instanceof String s) {
            return spu.readBoolean(di, s);
        }
        if (z instanceof Number n) {
            return n.doubleValue() == 0.0 ? Boolean.TRUE : Boolean.FALSE;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Double.valueOf(n.doubleValue());
        }
        if (z instanceof String s) {
            return Double.valueOf(spu.readPrimitiveDouble(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Float.valueOf(n.floatValue());
        }
        if (z instanceof String s) {
            return Float.valueOf(spu.readPrimitiveFloat(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Long.valueOf(n.longValue());
        }
        if (z instanceof String s) {
            return Long.valueOf(spu.readPrimitiveLong(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Integer.valueOf(n.intValue());
        }
        if (z instanceof String s) {
            return Integer.valueOf(spu.readPrimitiveInteger(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Short.valueOf(n.shortValue());
        }
        if (z instanceof String s) {
            return Short.valueOf(spu.readPrimitiveShort(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            return Byte.valueOf(n.byteValue());
        }
        if (z instanceof String s) {
            return Byte.valueOf(spu.readPrimitiveByte(di, s));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            if (z instanceof BigInteger bi)       // todo: check scale?
                return bi;
            if (z instanceof Integer i)
                return BigInteger.valueOf(i.intValue());
            if (z instanceof Long l)
                return BigInteger.valueOf(l.longValue());
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        if (z instanceof String s) {
            return spu.readBigInteger(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number) {
            if (z instanceof BigDecimal bd)       // todo: check scale?
                return bd;
            if (z instanceof FixedPointBase fpb)
                return fpb.toBigDecimal();
            if (z instanceof Integer i)
                return BigDecimal.valueOf(i.intValue());
            if (z instanceof Long l)
                return BigDecimal.valueOf(l.longValue());
            if (z instanceof Double d)
                return BigDecimal.valueOf(d.doubleValue());
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        if (z instanceof String s) {
            return spu.readBigDecimal(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public <F extends FixedPointBase<F>> F readFixedPoint(BasicNumericElementaryDataItem di, LongFunction<F> factory) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Number n) {
            long mantissa;
            if (z instanceof FixedPointBase x) {
                if (x.scale() == di.getDecimalDigits() && x.isFixedScale()) {
                    // this must be the same class - return as is
                    return (F)x;
                } else {
                    // is fixed point already, but with wrong scale
                    mantissa = FixedPointBase.mantissaFor(x.getMantissa(), x.scale(), di.getDecimalDigits(), di.getRounding());
                }
            }
            if (z instanceof BigDecimal bd) {
                mantissa = FixedPointBase.mantissaFor(bd.unscaledValue().longValue(), bd.scale(), di.getDecimalDigits(), di.getRounding());
            } else if (z instanceof Integer i) {
                mantissa = FixedPointBase.mantissaFor(i.longValue(), 0, di.getDecimalDigits(), false);
            } else if (z instanceof Long l) {
                mantissa = FixedPointBase.mantissaFor(l.longValue(), 0, di.getDecimalDigits(), false);
            } else {
                // anything else convert via double
                mantissa = FixedPointBase.mantissaFor(n.doubleValue(), di.getDecimalDigits());
            }
            return BigDecimalTools.check(factory.apply(mantissa), di, -1, currentClass);
        }
        if (z instanceof String s) {
            return BigDecimalTools.check(factory.apply(FixedPointBase.mantissaFor(s, di.getDecimalDigits())), di, -1, currentClass);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String s) {
            return spu.readAscii(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String s) {
            return spu.readString(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof ByteArray zz) {
            if (zz.length() > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz;
        }
        if (z instanceof byte [] zz) {
            if (zz.length > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz.length == 0 ? ByteArray.ZERO_BYTE_ARRAY : new ByteArray(zz);
        }
        if (z instanceof String s) {
            return spu.readByteArray(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof ByteArray zz) {
            if (zz.length() > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz.getBytes();
        }
        if (z instanceof byte [] zz) {
            if (zz.length > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di);
            return zz;
        }
        if (z instanceof String s) {
            return spu.readRaw(di, s);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof Instant i) {
            return i;
        }
        if (z instanceof String s) {
            return spu.readInstant(di, s);      // assumes precision = 1 second, with fractionals if ms
        }
        if (z instanceof Number n) {
            // convert number of seconds to Instant
            return Instant.ofEpochMilli((long)(n.doubleValue() * 1000.0));
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String s) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                return LocalDate.parse(s);
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalDate ld) {
            return ld;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String s) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                //return LocalTime.parse((String)z, di.getFractionalSeconds() > 0 ? ISODateTimeFormat.time() : ISODateTimeFormat.timeNoMillis());
                return LocalTime.parse(s);   // a more flexible parser
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalTime lt) {
            return lt;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        Object z = get(di);
        if (z == null)
            return null;
        if (z instanceof String s) {
            // cannot use spu, use JSON instead of Bonaparte formatting
            try {
                // return LocalDateTime.parse((String)z, di.getFractionalSeconds() > 0 ? ISODateTimeFormat.dateTime() : ISODateTimeFormat.dateTimeNoMillis());
                return LocalDateTime.parse(s);   // a more flexible parser
            } catch (IllegalArgumentException e) {
                throw err(MessageParserException.ILLEGAL_TIME, di);
            }
        }
        if (z instanceof LocalDateTime ldt) {
            return ldt;
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
        if (z instanceof Map<?,?> m) {
             // recursive invocation. This creates a new instance of MapParser, with same settings
             // an alternative implementation could remove the "final" qualifier from map, push it, and continue with the same parser instance
             Map subMap = m;
             BonaPortable subObj = allocObject(subMap, di);
             subObj.deserialize(new MapParser(subMap, instantInMillis, readEnumOrdinals, readEnumTokens));
             obj = subObj;
        } else if (z instanceof BonaCustom bc) {
            obj = bc;
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
        if (z instanceof BonaCustom bc) {
            return MapComposer.marshal(bc);
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
        if (z instanceof Object [] oarr) {
            return new ArrayList<Object>(Arrays.asList(oarr));
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
        BonaPortable obj = allocObject(map, StaticMeta.OUTER_BONAPORTABLE);
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
        if (z instanceof String s) {
            final T value = readEnumTokens ? factory.getByToken(s) : factory.getByName((String)z);
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
        if (z instanceof Number n) {
            return Integer.valueOf(n.intValue());
        }
        if (z instanceof String s) {
            if (readEnumOrdinals) {
                return Integer.valueOf(spu.readPrimitiveInteger(di, s));
            }
            // expect a name, and map that to the ordinal
            int ordinal = edi.getBaseEnum().getIds().indexOf(z);
            if (ordinal < 0) {
                throw err(MessageParserException.INVALID_ENUM_NAME, di);
            }
            return ordinal;
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, edi);
    }

    // special handling of enums: access the original name, not the one with $token suffix
    @Override
    public String readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws MessageParserException {
        Object z = get(edi);
        if (z == null)
            return null;
        if (z instanceof String s) {
            if (readEnumTokens) {
                return spu.readString(di, s);
            }
            // expect a name, and map it to the token
            int ordinal = edi.getBaseEnum().getIds().indexOf(z);
            if (ordinal < 0) {
                throw err(MessageParserException.INVALID_ENUM_NAME, di);
            }
            // convert the name into a token
            return edi.getBaseEnum().getTokens().get(ordinal);
        }
        throw err(MessageParserException.UNSUPPORTED_CONVERSION, edi);
    }

    // enumset conversions

    protected String mapEnumTokens(FieldDefinition di, EnumDefinition edi) {
        final Collection<?> values = getCollection(di);
        if (values == null)
            return null;
        final ArrayList<String> tokens = new ArrayList<>(values.size());
        List<String> instances = edi.getIds();
        for (Object v: values) {
            int ordinal = instances.indexOf(v);
            if (ordinal < 0) {
                throw err(MessageParserException.INVALID_ENUM_NAME, di);
            }
            tokens.add(edi.getTokens().get(ordinal));
        }
        Collections.sort(tokens);
        StringBuilder buff = new StringBuilder(tokens.size());
        for (String s: tokens) {
            buff.append(s);
        }
        return buff.toString();
    }

    @Override
    public String readString4Xenumset(XEnumSetDataItem di) throws MessageParserException {
        if (readEnumTokens) {
            return readString(di);
        } else {
            // compose the set
            // read instance names and map to tokens
            // FIXME: currently maps values of the base enum only, must obtain dynamically collected enums
            EnumDefinition edi = di.getBaseXEnumset().getBaseXEnum().getBaseEnum();
            return mapEnumTokens(di, edi);
        }
    }

    @Override
    public String readString4EnumSet(AlphanumericEnumSetDataItem di) throws MessageParserException {
        if (readEnumTokens) {
            return readString(di);
        } else {
            // compose the set
            // read instance names and map to tokens
            EnumDefinition edi = di.getBaseEnumset().getBaseEnum();
            return mapEnumTokens(di, edi);
        }
    }

    protected Collection<?>  getCollection(FieldDefinition di) {
        Object enumset = get(di);
        if (enumset == null)
            return null;
        if (!(enumset instanceof Collection)) {
            throw err(MessageParserException.UNSUPPORTED_CONVERSION, di);
        }
        return (Collection<?>)enumset;
    }

    protected long mapToBitmap(NumericEnumSetDataItem di, Collection<?> values) {
        long bitmap = 0L;
        List<String> instances = di.getBaseEnumset().getBaseEnum().getIds();
        for (Object v: values) {
            int ordinal = instances.indexOf(v);
            if (ordinal < 0) {
                throw err(MessageParserException.INVALID_ENUM_NAME, di);
            }
            bitmap |= (1L << ordinal);
        }
        return bitmap;
    }

    @Override
    public Long readLong4EnumSet(NumericEnumSetDataItem di) throws MessageParserException {
        if (readEnumOrdinals) {
            return readLong(di);
        } else {
            // read instance names and map to ordinals
            final Collection<?> values = getCollection(di);
            if (values == null)
                return null;
            return Long.valueOf(mapToBitmap(di, values));
        }
    }

    @Override
    public Integer readInteger4EnumSet(NumericEnumSetDataItem di) throws MessageParserException {
        if (readEnumOrdinals) {
            return readInteger(di);
        } else {
            // read instance names and map to ordinals
            final Collection<?> values = getCollection(di);
            if (values == null)
                return null;
            return Integer.valueOf((int) mapToBitmap(di, values));
        }
    }

    @Override
    public Short readShort4EnumSet(NumericEnumSetDataItem di) throws MessageParserException {
        if (readEnumOrdinals) {
            return readShort(di);
        } else {
            // read instance names and map to ordinals
            final Collection<?> values = getCollection(di);
            if (values == null)
                return null;
            return Short.valueOf((short) mapToBitmap(di, values));
        }
    }

    @Override
    public Byte readByte4EnumSet(NumericEnumSetDataItem di) throws MessageParserException {
        if (readEnumOrdinals) {
            return readByte(di);
        } else {
            // read instance names and map to ordinals
            final Collection<?> values = getCollection(di);
            if (values == null)
                return null;
            return Byte.valueOf((byte) mapToBitmap(di, values));
        }
    }
}
