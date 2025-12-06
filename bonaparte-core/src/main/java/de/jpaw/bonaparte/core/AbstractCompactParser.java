package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.bonaparte.util.DayTime;
import de.jpaw.bonaparte.util.FreezeTools;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;


public abstract class AbstractCompactParser<E extends Exception>  extends Settings implements MessageParser<E>, CompactConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCompactParser.class);
    protected static final byte [] EMPTY_BYTE_ARRAY = new byte [0];

    // most of these are available from DataInput, but need an Exception mapper
    abstract protected boolean atEnd() throws E;                // test for end of input - can be slow (using exceptions for some implementations, therefore only use if no other method exists)
    abstract protected void pushback(int c);                    // push back a single token, which must be the last byte read, and can only be consumed by a subsequent needToken() (both flavours)
    abstract protected E newMPE(int n, String msg);             // construct a suitable exception
    abstract protected BonaPortable createObject(String classname) throws E;             // same method - overloading required for possible exception mapping
    abstract protected BigDecimal checkAndScale(BigDecimal num, NumericElementaryDataItem di) throws E;       // exception mapper

    // basic methods to read data from the stream - all of them will throw an exception if the end of the input has been reached
    abstract protected int needToken() throws E;                // single byte as unsigned (or pushed back character)
    abstract protected void needToken(int c) throws E;          // single byte as unsigned (or pushed back character)
    abstract protected void skipBytes(int n) throws E;
    abstract protected long readFixed8ByteLong() throws E;
    abstract protected long readFixed6ByteLong() throws E;
    abstract protected int readFixed4ByteInt() throws E;
    abstract protected int readFixed3ByteInt() throws E;
    abstract protected int readFixed2ByteInt() throws E;
    abstract protected char readChar() throws E;
    abstract protected byte [] readBytes(int n) throws E;       // read exactly n bytes and return them in some new byte array
    abstract protected ByteArray readByteArray(int n) throws E; // read exactly n bytes and return them in some new byte array (avoids array copy)
    abstract protected String readISO(int len) throws E;        // could be coded in general, but provided for performance
    abstract protected String readUTF16(int len) throws E;      // could be coded in general, but provided for performance
    abstract protected String readUTF8(int len) throws E;       // could be coded in general, but provided for performance

    protected String currentClass;
    private final boolean useCache = true;
    private List<BonaPortable> objects;
//    private int skipDepth = 0;

    protected AbstractCompactParser() {
        if (useCache)
            objects = new ArrayList<BonaPortable>(60);
    }

    protected void clearCache() {
        if (useCache)
            objects.clear();
    }

    // provide a parser position, if possible. Only used for diagnostic output, return -1 if not available
    protected int getParseIndex() {
        return -1;
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }


    @Override
    public E enumExceptionConverter(IllegalArgumentException e) {
        return newMPE(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage());
    }

    @Override
    public E customExceptionConverter(String msg, Exception e) {
        return newMPE(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg);
    }

    private E eNotNumeric(int n, String fieldname) {
        return newMPE(MessageParserException.NUMBER_PARSING_ERROR, "Numeric token expected but got " + (n & 0xff) + " for field " + currentClass + "." + fieldname);
    }

    /** Check for Null. Returns true if null has been encountered and was allowed. Throws an exception in case it was not allowed. Returns false
     * if no null is next. (Called for field members inside a class.)
     */
    protected boolean checkForNull(FieldDefinition di) throws E {
        return checkForNull(di.getName(), di.getIsRequired());
    }

    // check for Null called for field members inside a class
    protected boolean checkForNull(String fieldname, boolean isRequired) throws E {
        int c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushback(c);
                return true;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname);
            }
        }
        pushback(c);
        return false;
    }

    // check for Null called for field members inside a class
    protected boolean checkForNullOrNeedToken(String fieldname, boolean isRequired, int token) throws E {
        int c = needToken();
        if (c == token)
            return false;
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushback(c);
                return true;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname);
            }
        }
        throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected 0x%02x, got 0x%02x)", token, c));
    }

    // get the next token, or -1 for explicit null or -2 for implicit null
    protected int nextToken(String fieldname, boolean isRequired) throws E {
        int c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return -1;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushback(c);
                return -2;
            } else {
                throw newMPE(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname);
            }
        }
        return c;
    }

    // differs to previous implementation: this method does not end if EOF is reached, is does require a subsequent token (for example object end or record end)
    protected void skipExplicitNulls() throws E {
        int c;
        while ((c = needToken()) == NULL_FIELD) {
        }
        pushback(c);
    }

    @Override
    public void eatParentSeparator() throws E {
        eatObjectOrParentSeparator(PARENT_SEPARATOR);
    }

    public void eatObjectTerminator() throws E {
        eatObjectOrParentSeparator(OBJECT_TERMINATOR);
    }

    protected void eatObjectOrParentSeparator(int which) throws E {
        skipExplicitNulls();  // upwards compatibility: skip extra fields if they are blank.
        int z = needToken();
        if (z == which)
            return;   // all good

        // we have extra data and it is not null. Now the behavior depends on a parser setting
        ParseSkipNonNulls mySetting = getSkipNonNullsBehavior();
        switch (mySetting) {
        case ERROR:
            throw newMPE(MessageParserException.EXTRA_FIELDS, String.format("(found byte 0x%02x)", z));
        case WARN:
            LOGGER.warn("{} at index {} parsing class {}", MessageParserException.codeToString(MessageParserException.EXTRA_FIELDS), getParseIndex(), currentClass);
            // fall through
        case IGNORE:
            // the byte encountered next (z) is not what we wanted. Skip non-null fields (or sub objects, even nested) until we find the desired terminator.
            // skip bytes until we are at end of record (bad!) (thrown by needToken()) or find the terminator
            pushback(z);   // ensure that the byte z is read again!
//            skipDepth = 0;
            skipUntilNext(which);
        }
    }

    /** Skips over the data until we find the expected token (usually a record terminator or object terminator or parent separator).
     * When the method returns, the parser is just behind the expected character. */
    protected void skipUntilNext(int which) throws E {
        int c;
        // System.out.println(String.format("Descend for skip depth %d for %02x", skipDepth, which));
//        ++skipDepth;
        while ((c = needToken()) != which) {
            // skip one element, unless the expected one has been found
            if (c < OBJECT_BEGIN_BASE)
                continue;  // single byte elements
            int skipBytes = SKIP_BYTES[c - OBJECT_BEGIN_BASE];
            if (skipBytes > 0) {
//                System.out.println(String.format("At pos %04x: ", parseIndex-1) + "skipping " + skipBytes + " bytes for token " + String.format("%02x", c));
//                if (c >= 0xb0 && c < 0xc0)
//                    System.out.println("    String is " + new String(inputdata, parseIndex, c - 0xb0 + 1));
                skipBytes(skipBytes);
            }
            if (skipBytes < 0) {
//                System.out.println(String.format("At pos %04x: ", parseIndex-1) + "special for token " + String.format("%02x", c));
                // special treatment bytes. These are cases where the length is dynamically determined, or which require recursive processing
                switch (c) {
                case OBJECT_BEGIN_JSON: // 0xab: new object (by string
                case OBJECT_BEGIN_BASE: // 0xac: new object (by string
                case OBJECT_BEGIN_ID:   // 0xde: 2 numeric, recurse!
                case OBJECT_BEGIN_PQON: // 0xdf: object / PQON
                    skipUntilNext(OBJECT_TERMINATOR);
                    break;
                case COMPRESSED:
                    throw newMPE(MessageParserException.UNSUPPORTED_COMPRESSED, null); // TODO: compressed object not yet supported
                case COMPACT_BIGINTEGER:
                case ISO_STRING:
                case COMPACT_BINARY:
                case UTF8_STRING:
//                        int len = readInt(needToken(), "(skipping)");
//                        System.out.println("    String is " + new String(inputdata, parseIndex, len));
//                        skipBytes(len);
                    skipBytes(readInt(needToken(), "(skipping)"));
                    break;
                case UTF16_STRING:
                    skipBytes(2 * readInt(needToken(), "(skipping UTF16)"));
                    break;
                default:
                    throw newMPE(MessageParserException.UNSUPPORTED_TOKEN, null); // TODO (-2 values...)
                }
            }
        }
//        --skipDepth;
        // System.out.println(String.format("Return at skip depth %d for %02x", skipDepth, which));
    }

    // upon entry, we know that firstByte is not null (0xa0)
    protected int readInt(int firstByte, String fieldname) throws E {
        if (firstByte < 0xa0) {
            // 1 positive byte numbers
            if (firstByte <= 31)
                return firstByte;
            if (firstByte >= 0x80)
                return firstByte - 0x60;  // 0x20..0x3f
            throw eNotNumeric(firstByte, fieldname);
        }
        if (firstByte <= 0xd0) {
            if (firstByte <= 0xaa)
                return 0xa0 - firstByte;  // -1 .. -10
            if (firstByte < 0xc0)
                throw eNotNumeric(firstByte, fieldname);
            // 2 byte number 0...2047
            return needToken() + ((firstByte & 0x0f) << 8);
        }
        switch (firstByte) {
        case INT_2BYTE:
            return readFixed2ByteInt();
        case INT_3BYTE:
            return readFixed3ByteInt();
        case INT_4BYTE:
            return readFixed4ByteInt();
        case COMPACT_BOOLEAN_FALSE:
            return 0;               // boolean => int upgrade
        case COMPACT_BOOLEAN_TRUE:
            return 1;               // boolean => int upgrade
        default:
            throw eNotNumeric(firstByte, fieldname);
        }
    }

    protected long readLong(int firstByte, String fieldname) throws E {
        if (firstByte == INT_6BYTE)
            return readFixed6ByteLong();
        if (firstByte == INT_8BYTE)
            return readFixed8ByteLong();
        return readInt(firstByte, fieldname);
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return readString(di.getName());
    }

    // read a non-null string
    protected String readString(String fieldname) throws E {
        int len;
        int c = needToken();
        if (c >= 0x20 && c < 0x80)
            return String.valueOf((char)c);  // single byte string
        if (c >= EMPTY_FIELD && c <= SHORT_ISO_STRING + 15) {
            len = c - EMPTY_FIELD;
            return len > 0 ? readISO(len) : "";
        }
        switch (c) {
        case UNICODE_CHAR:
            return String.valueOf(readChar()); // single Unicode char string
        case ISO_STRING:
            len = readInt(needToken(), fieldname);
            return readISO(len);
        case UTF8_STRING:
            len = readInt(needToken(), fieldname);
            return readUTF8(len);
        case UTF16_STRING:
            len = readInt(needToken(), fieldname);
            return readUTF16(len);
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected STRING*, got 0x%02x)", c));
        }
    }


    @Override
    public String readString(AlphanumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return readString(di.getName());
    }


    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return ByteArray.ZERO_BYTE_ARRAY;       // pre 3.6.0 compatibility
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            return readByteArray(len);
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected BINARY*, got 0x%02x)", c));
        }
    }


    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return EMPTY_BYTE_ARRAY;            // pre 3.6.0 compatibility
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            return readBytes(len);
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected BINARY*, got 0x%02x)", c));
        }
    }

    // have the scale already
    protected BigDecimal readBigdec(int scale, String fieldname) throws E {
        int c = needToken();
        if (c == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), fieldname);
            return new BigDecimal(new BigInteger(readBytes(len)), scale);
        } else {
            return BigDecimal.valueOf(readLong(c, fieldname), scale);
        }
    }

    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        String fieldname = di.getName();
        int scale = 0;
        int c = needToken();
        if (c == 0)
            return BigDecimal.ZERO;
        if (c >= COMPACT_BIGDECIMAL && c <= COMPACT_BIGDECIMAL + 9) {
            // BigDecimal with scale
            if (c != COMPACT_BIGDECIMAL) {
                scale = c - COMPACT_BIGDECIMAL;
            } else {
                scale = readInt(needToken(), fieldname);
            }
        } else {
            pushback(c);
        }
        // now read mantissa. Either length  + digits, or an integer
        return checkAndScale(readBigdec(scale, fieldname), di);
    }


    @Override
    public <F extends FixedPointBase<F>> F readFixedPoint(BasicNumericElementaryDataItem di, LongFunction<F> factory) throws E {
        if (checkForNull(di))
            return null;
        String fieldname = di.getName();
        int scale = 0;
        int c = needToken();
        if (c >= COMPACT_BIGDECIMAL && c <= COMPACT_BIGDECIMAL + 9) {
            // BigDecimal with scale
            if (c != COMPACT_BIGDECIMAL) {
                scale = c - COMPACT_BIGDECIMAL;
            } else {
                scale = readInt(needToken(), fieldname);
            }
        } else {
            pushback(c);
        }
        // now read mantissa. Either length  + digits, or an integer
        final int c2 = needToken();
        final long mantissa;
        if (c2 == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), fieldname);
            mantissa = new BigInteger(readBytes(len)).longValue();
        } else {
            mantissa = readLong(c2, fieldname);
        }
        return BigDecimalTools.check(factory.apply(FixedPointBase.mantissaFor(mantissa, scale, di.getDecimalDigits(), di.getRounding())), di, -1, currentClass);
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return Character.valueOf(readPrimitiveCharacter(di));
    }


    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == 0 || c == COMPACT_BOOLEAN_FALSE)
            return Boolean.FALSE;
        if (c == 1 || c == COMPACT_BOOLEAN_TRUE)
            return Boolean.TRUE;
        throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected BOOLEAN 0/1 or false/true, got 0x%02x)", c));
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return Double.valueOf(readPrimitiveDouble(di));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return Float.valueOf(readPrimitiveFloat(di));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return readLong(needToken(), di.getName());
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return readInt(needToken(), di.getName());
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return (short)readInt(needToken(), di.getName());
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return (byte)readInt(needToken(), di.getName());
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), di.getName());
            return new BigInteger(readBytes(len));
        } else {
            if (c == 0)
                return BigInteger.ZERO;
            return BigInteger.valueOf(readLong(c, di.getName()));
        }
    }

    protected LocalDate readDate(String fieldname) throws E {
        int year = readInt(needToken(), fieldname);
        int month = readInt(needToken(), fieldname);
        int day = readInt(needToken(), fieldname);
        return LocalDate.of(year, month, day);
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws E {
        if (checkForNullOrNeedToken(di.getName(), di.getIsRequired(), COMPACT_DATE))
            return null;
        return readDate(di.getName());
    }


    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case COMPACT_TIME_MILLIS:
            return DayTime.timeForMillis(readInt(needToken(), di.getName()));
        case COMPACT_TIME:
            return LocalTime.ofSecondOfDay(readInt(needToken(), di.getName()));
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected COMPACT_TIME_*, got 0x%02x)", c));
        }
    }

    protected LocalDateTime readDateTime(String fieldname, boolean fractional) throws E {
        int year = readInt(needToken(), fieldname);
        int month = readInt(needToken(), fieldname);
        int day = readInt(needToken(), fieldname);
        int secondsOfDay = readInt(needToken(), fieldname);
        int millis = 0;
        if (fractional) {
            millis = secondsOfDay % 1000;
            secondsOfDay /= 1000;
        }
        return LocalDateTime.of(year, month, day, secondsOfDay / 3600, (secondsOfDay % 3600) / 60, secondsOfDay % 60, millis * 1000000);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case COMPACT_DATETIME:
            return readDateTime(di.getName(), false);
        case COMPACT_DATETIME_MILLIS:
            return readDateTime(di.getName(), true);
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected COMPACT_DATETIME_*, got 0x%02x)", c));
        }
    }


    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws E {
        if (checkForNull(di))
            return null;
        return Instant.ofEpochMilli(readLong(needToken(), di.getName()));
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws E {
        XEnumDefinition spec = di.getBaseXEnum();
        if (checkForNull(di.getName(), di.getIsRequired() && !spec.getHasNullToken()))
            return factory.getNullToken();
        String scannedToken = readString(di.getName());
        T value = factory.getByToken(scannedToken);
        if (value == null) {
            throw newMPE(MessageParserException.INVALID_ENUM_TOKEN, scannedToken);
        }
        return value;
    }

    protected UUID readUUID() throws E {
        long msl = readFixed8ByteLong();
        long lsl = readFixed8ByteLong();
        return new UUID(msl, lsl);
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws E {
        if (checkForNullOrNeedToken(di.getName(), di.getIsRequired(), COMPACT_UUID))
            return null;
        return readUUID();
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws E {
        int c = needToken();
        if (c == COMPACT_DOUBLE) {
            return Double.longBitsToDouble(readFixed8ByteLong());
        }
        // not a float, try upgrade of int to double (doubles of value 0 or 1 are explicitly written as int)
        return readInt(c, di.getName());
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws E {
        int c = needToken();
        if (c == COMPACT_FLOAT) {
            return Float.intBitsToFloat(readFixed4ByteInt());
        }
        // not a float, try upgrade of int to float (floats of value 0 or 1 are explicitly written as int)
        return readInt(c, di.getName());
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws E {
        return readLong(needToken(), di.getName());
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws E {
        return readInt(needToken(), di.getName());
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws E {
        return (short)readInt(needToken(), di.getName());
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws E {
        return (byte)readInt(needToken(), di.getName());
    }

    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws E {
        if (checkForNull(di)) {
            return null;
        }
        boolean allowSubtypes = di.getAllowSubclasses();
        String fieldname = di.getName();
        int c = needToken();
        if (useCache && c == OBJECT_AGAIN) {
            // we reuse an object
            int objectIndex = readInt(needToken(), fieldname);
            if (objectIndex >= objects.size())
                throw newMPE(MessageParserException.INVALID_BACKREFERENCE, String.format("at %s: requested object %d of only %d available", fieldname, objectIndex, objects.size()));
            BonaPortable newObject = objects.get(objects.size() - 1 - objectIndex);  // 0 is the last one put in, 1 the one before last etc...
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw newMPE(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes));
                }
            }
            return type.cast(newObject);
        } else if (c == OBJECT_BEGIN_PQON || c == OBJECT_BEGIN_ID || c == OBJECT_BEGIN_BASE) {
            String previousClass = currentClass;
            BonaPortable newObject;
            String classname;
            if (c == OBJECT_BEGIN_ID) {
                int factoryId = readInt(needToken(), "$factoryId");
                int classId = readInt(needToken(), "$classId");
                BonaPortableClass<?> bclass = BonaPortableFactoryById.getByIds(factoryId, classId);
                if (bclass == null)
                    throw newMPE(MessageParserException.BAD_CLASS_IDS, factoryId + "/" + classId);
                classname = bclass.getPqon();
                newObject = bclass.newInstance();
            } else {
                if (c == OBJECT_BEGIN_BASE) {
                    if (di.getLowerBound() == null)
                        throw newMPE(MessageParserException.INVALID_BASE_CLASS_REFERENCE, "");
                    classname = di.getLowerBound().getName();
                } else {
                    classname = readString(fieldname);
                    if (classname == null || classname.length() == 0) {
                        if (di.getLowerBound() == null)
                            throw newMPE(MessageParserException.INVALID_BASE_CLASS_REFERENCE, "");
                        // the base class name is referred to, which is contained in the meta data
                        classname = di.getLowerBound().getName();
                    }
                    needToken(NULL_FIELD); // version not yet allowed
                }
                newObject = createObject(classname);
            }
            // System.out.println("Creating new obj " + classname + " gave me " + newObject);
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw newMPE(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes));
                }
            }
            // all good here. Parse the contents
            // if we use the cache, make the object known even before the contents has been parsed, because it may be referenced if the structure is cyclic
            if (useCache)
                objects.add(newObject);

            currentClass = classname;
            newObject.deserialize(this);
            eatObjectTerminator();
            currentClass = previousClass;
            return type.cast(newObject);
        } else {
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected OBJECT_START*, got 0x%02x)", c));
        }
    }


    @Override
    public int parseMapStart(FieldDefinition di) throws E {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), MAP_BEGIN))
            return COLLECTION_COUNT_NULL;
        return readInt(needToken(), di.getName());
    }


    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws E {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), ARRAY_BEGIN))
            return COLLECTION_COUNT_NULL;
        return readInt(needToken(), di.getName());
    }


    @Override
    public void parseArrayEnd() throws E {
    }


    @Override
    public BonaPortable readRecord() throws E {
        // there are no record start/end markers in this format
        return readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }


    @Override
    public List<BonaPortable> readTransmission() throws E {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        while (!atEnd())
            results.add(readRecord());
        return results;
    }


    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws E {
        int c = needToken();
        if (c == 0 || c == COMPACT_BOOLEAN_FALSE)
            return false;
        if (c == 1 || c == COMPACT_BOOLEAN_TRUE)
            return true;
        throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected BOOLEAN 0/1 or false/true, got 0x%02x)", c));
    }

    // default implementations for the next ones...
    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws E {
        int c = needToken();
        if (c >= 0x20 && c < 0x80)
            return (char)c;         // single byte char (ASCII printable)
        if (c == SHORT_ISO_STRING)  // 1 char ISO string
            return (char)needToken();
        if (c != UNICODE_CHAR)
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected UNICODE_CHAR, got 0x%02x)", c));
        return readChar();
    }

    protected void addMapElem(Map<String, Object> map) throws E {
        // parse field name, then value
        String key = readString("$jsonObjKey"); // an explicit or implicit null will cause an exception
        // do not check for validity of the key here, because outside actual JavaScript, it is frequently used
//        if (!CharTestsASCII.isJavascriptId(key)) {
//            throw newMPE(MessageParserException.JSON_ID, key);
//        }
        // now read value
        final Object value = readElementSub();
        if (map.put(key, value) != null) {
            throw newMPE(MessageParserException.JSON_DUPLICATE_KEY, key);
        }
    }

    // read a non-null map with the start character already parsed
    protected Map<String, Object> readJsonMapFlexSizeSub() throws E {
        final Map<String, Object> map = new HashMap<String, Object>();
        // now iterate until JSON end is found
        for (;;) {
            int c = needToken();
            if (c == OBJECT_TERMINATOR) {
                return map;
            }
            pushback(c);
            addMapElem(map);
        }
    }

    // read a JSON object: Either a flex map or a null is expected here
    @Override
    public Map<String, Object> readJson(ObjectReference di) throws E {
        int c = nextToken(di.getName(), di.getIsRequired());
        switch (c) {
        case -1:
        case -2:
            return null;
        case OBJECT_BEGIN_JSON:
            return readJsonMapFlexSizeSub();
        case MAP_BEGIN:
            return readMapFixedSizeSub();
        default:
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected object start 0xab or 0xfa, got 0x%02x)", c));
        }
    }

    // read a JSON array: Either an array or a null is expected here
    @Override
    public List<Object> readArray(ObjectReference di) throws E {
        if (checkForNullOrNeedToken(di.getName(), di.getIsRequired(),  ARRAY_BEGIN))
            return null;
        // not null, therefore JSON begin
        return readArraySub();
    }

    protected List<Object> readArraySub() throws E {
        final int numElem = readInt(needToken(), "$jsonArrayNumElem");
        final List<Object> l = new ArrayList<Object>(numElem);
        for (int i = 0; i < numElem; ++i)
            l.add(readElementSub());
        return l;
    }

    protected Map<String, Object> readMapFixedSizeSub() throws E {
        final int numElem = readInt(needToken(), "$jsonMapNumElem");
        final Map<String, Object> map = new HashMap<String, Object>(FreezeTools.getInitialHashMapCapacity(numElem));
        for (int i = 0; i < numElem; ++i) {
            addMapElem(map);
        }
        return map;
    }

    private static final Object [] CONSTANT_OBJECTS = new Object[0xab];
    static {
        int i;
        for (i = 0; i < 32; ++i)
            CONSTANT_OBJECTS[i] = Integer.valueOf(i);
        for (i = 32; i < 128; ++i)
            CONSTANT_OBJECTS[i] = String.valueOf((char)i);
        for (i = 0x80; i <= 0x9c; ++i)
            CONSTANT_OBJECTS[i] = Integer.valueOf(i - 96); // 128 - 32;
        CONSTANT_OBJECTS[0x9d]  = null;   // RESERVED
        CONSTANT_OBJECTS[0x9e]  = Boolean.FALSE;
        CONSTANT_OBJECTS[0x9f]  = Boolean.TRUE;
        CONSTANT_OBJECTS[0xa0]  = null;
        for (i = 0xa1; i <= 0xaa; ++i)
            CONSTANT_OBJECTS[i] = Integer.valueOf(-(i - 0xa0)); // -1..-10

    }

    // read an optional element
    protected Object readElementSub() throws E {
        int c = needToken();
        if (c <= 0xaa) {
            // single byte items
            return CONSTANT_OBJECTS[c];
        }
        if (c <= 0xcf) {
            if (c >= 0xb0) {
                if (c <= 0xbf) {
                    // short single byte string
                    return readISO(c - 0xaf);
                } else {
                    // 2 byte positive integer: 4 bits + 8
                    return Integer.valueOf(((c & 15) << 8) | needToken());
                }
            }
            switch (c) {
            case OBJECT_BEGIN_JSON:         //0xab
                return readJsonMapFlexSizeSub();
            case OBJECT_BEGIN_BASE:         //0xac
                // cannot happen within JSON, except if the structure has been redeclared.
                throw newMPE(MessageParserException.INVALID_BACKREFERENCE, "in JSON element");
            case OBJECT_TERMINATOR:         //0xad
                // treat as implicit null: fall through
            case PARENT_SEPARATOR:          //0xae
                // treat as implicit null
                pushback(c);
                return null;
            case EMPTY_FIELD:               //0xaf
                return EMPTY_STRING;
            }
        }

        // remaining tokens are 0xd0 .. 0xff
        // hope the compiler converts it to a jump table
        switch (c) {
        case COMPACT_FLOAT:                 //0xd1
            return Float.intBitsToFloat(readFixed4ByteInt());
        case COMPACT_DOUBLE:                //0xd2
            return Double.longBitsToDouble(readFixed8ByteLong());
        case UNICODE_CHAR:                  //0xd6
            return String.valueOf(readChar());
        case COMPACT_UUID:                  //0xd7
            return readUUID();
        case COMPACT_DATE:                  //0xd8
            return readDate("$jsonElemDate");
        case COMPACT_TIME:                  //0xd9
            return LocalTime.ofSecondOfDay(readInt(needToken(), "$jsonElemTime"));
        case COMPACT_TIME_MILLIS:           //0xda
            return DayTime.timeForMillis(readInt(needToken(), "$jsonElemTimeMs"));
        case COMPACT_DATETIME:              //0xdb
            return readDateTime("$jsonElemDateTime", false);
        case COMPACT_DATETIME_MILLIS:       //0xdc
            return readDateTime("$jsonElemDateTimeMs", true);

        case OBJECT_AGAIN:                  //0xdd
        case OBJECT_BEGIN_ID:               //0xde
        case OBJECT_BEGIN_PQON:             //0xdf
            // object within JSON
            pushback(c);
            return readObject(StaticMeta.INNER_BONAPORTABLE, BonaPortable.class);

        case COMPACT_BIGINTEGER:            //0xe0
            {
                int len = readInt(needToken(), "$jsonElemBigintLen");
                return new BigInteger(readBytes(len));
            }
        case ISO_STRING:                    //0xe1
            {
                int len = readInt(needToken(), "$jsonLenISO");
                return readISO(len);
            }
        case INT_2BYTE:                     //0xe2
            return Integer.valueOf(readFixed2ByteInt());
        case INT_3BYTE:                     //0xe3
            return Integer.valueOf(readFixed3ByteInt());
        case INT_4BYTE:                     //0xe4
            return Integer.valueOf(readFixed4ByteInt());
        case INT_6BYTE:                     //0xe6
            return Long.valueOf(readFixed6ByteLong());
        case INT_8BYTE:                     //0xe8
            return Long.valueOf(readFixed8ByteLong());

        case COMPACT_BIGDECIMAL:            //0xf0
            {
                int scale = readInt(needToken(), "$jsonBigdecScale");
                return readBigdec(scale, "$jsonBigdecMant");
            }
        case 0xf1:
        case 0xf2:
        case 0xf3:
        case 0xf4:
        case 0xf5:
        case 0xf6:
        case 0xf7:
        case 0xf8:
        case 0xf9:
            return readBigdec(c - 0xf0, "$jsonBigdecMant");
        case MAP_BEGIN:                     //0xfa
            return readMapFixedSizeSub();
        case ARRAY_BEGIN:                   //0xfc
            return readArraySub();
        case UTF16_STRING:                  //0xfd
            {
                int len = readInt(needToken(), "$jsonLenUTF16");
                return readUTF16(len);
            }
        case COMPACT_BINARY:                //0xfe
            {
                int len = readInt(needToken(), "$jsonByteArray");
                return readByteArray(len);      // or readBytes.... / Q: return as [] or ByteArray?
            }
        case UTF8_STRING :                  //0xff
            {
                int len = readInt(needToken(), "$jsonLenUTF8");
                return readUTF8(len);
            }

        default:  // compressed, unsupported float types etc...
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("0x%02x in JSON element", c));
        }
    }

    // reads a single element
    @Override
    public Object readElement(ObjectReference di) throws E {
        return readElementSub();
    }

    @Override
    public Integer readEnum(EnumDataItem edi, BasicNumericElementaryDataItem di) throws E {
        return readInteger(di);
    }

    @Override
    public String readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws E {
        return readString(di);
    }
}
