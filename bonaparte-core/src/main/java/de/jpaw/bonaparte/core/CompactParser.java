package de.jpaw.bonaparte.core;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.ByteArray;

public class CompactParser extends CompactConstants implements MessageParser<IOException> {
    private static final byte [] EMPTY_BYTE_ARRAY = new byte [0];
    private static final String EMPTY_STRING = "";
    
    protected final DataInput in;
    protected final boolean useCache = true;
    protected List<BonaPortable> objects;
    private String currentClass;
    private int pushedBack = -1;
    
    public static void deserialize(BonaPortable obj, DataInput _in) throws IOException {
        obj.deserialize(new CompactParser(_in));
    }

    public CompactParser(DataInput in) {
        this.in = in;
        if (useCache)
            objects = new ArrayList<BonaPortable>(60);
    }
    
    private int needToken() throws IOException {
        if (pushedBack >= 0) {
            int c = pushedBack;
            pushedBack = -1;
            return c;
        }
        return in.readUnsignedByte();
    }

    private void needToken(int c) throws IOException {
        int d = needToken();
        if (c != d) {
            throw new IOException(String.format("expected 0x%02x, got 0x%02x in class %s", c, d, currentClass));
        }
    }

    private void skipNulls() throws IOException {
        int c;
        while ((c = needToken()) == NULL_FIELD) {
        }
        pushedBack = c;
    }
    
    // check for Null called for field members inside a class
    private boolean checkForNullOrNeedToken(FieldDefinition di, int token) throws IOException {
        return checkForNullOrNeedToken(di.getName(), di.getIsRequired(), token);
    }
    // check for Null called for field members inside a class
    private boolean checkForNullOrNeedToken(String fieldname, boolean isRequired, int token) throws IOException {
        int c = needToken();
        if (c == token)
            return false;
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new IOException("Illegal explicit null for " + currentClass + "." + fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == COLLECTIONS_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushedBack = c;
                return true;
            } else {
                throw new IOException("Illegal implicit null for " + currentClass + "." + fieldname);
            }
        }
        throw new IOException("Unexpected character: " + String.format("(expected 0x%02x, got 0x%02x)", token, c) + " for " + currentClass + "." + fieldname);
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean isRequired) throws IOException {
        int c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new IOException("Null not allowed for " + currentClass + "." + fieldname + " (found explicit null token)");
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == COLLECTIONS_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushedBack = c;
                return true;
            } else {
                throw new IOException("Null not allowed for " + currentClass + "." + fieldname + " (found implicit null)");
            }
        }
        pushedBack = c;
        return false;
    }
    private boolean checkForNull(FieldDefinition di) throws IOException {
        return checkForNull(di.getName(), di.getIsRequired());
    }
    
    private void eNotNumeric(int n, String fieldname) throws IOException {
        throw new IOException("Numeric token expected but got " + (n & 0xff) + " for field " + currentClass + "." + fieldname);
    }
    
    // upon entry, we know that firstByte is not null (0xa0)
    private int readInt(int firstByte, String fieldname) throws IOException {
        if (firstByte < 0xa0) {
            // 1 positive byte numbers 
            if (firstByte <= 31)
                return firstByte;
            if (firstByte >= 0x80)
                return firstByte - 0x60;  // 0x20..0x3f     // TODO: currently 61..63 are accepted, later deprecated these
            eNotNumeric(firstByte, fieldname);
        }
        if (firstByte <= 0xd0) {
            if (firstByte <= 0xac)
                return 0xa0 - firstByte;  // -1 .. -10      // TODO: currently still -11, -12, later deprecated these
            if (firstByte < 0xc0)
                eNotNumeric(firstByte, fieldname);
            // 2 byte number 0...2047
            return needToken() + ((firstByte & 0x0f) << 8);
        }
        switch (firstByte) {
        case INT_2BYTE:
            return in.readShort();
        case INT_3BYTE:
            int nn = in.readUnsignedByte() << 16;
            nn |= in.readUnsignedShort();
            if ((nn & 0x800000) != 0)
                nn |= 0xff << 24;   // sign-extend
            return nn;
        case INT_4BYTE:
            return readFixed4ByteInt();
        default:
            eNotNumeric(firstByte, fieldname);
        }
        return 0;  // unreached....
    }

    private int readFixed4ByteInt() throws IOException {
        return in.readInt();
    }
    
    private long readFixed6ByteLong() throws IOException {
        int nn1 = in.readShort();
        return (long)nn1 << 32 | (in.readInt() & 0xffffffffL);
    }
    private long readFixed8ByteLong() throws IOException {
        return in.readLong();
    }
    private long readLong(int firstByte, String fieldname) throws IOException {
        if (firstByte == INT_6BYTE)
            return readFixed6ByteLong();
        if (firstByte == INT_8BYTE)
            return readFixed8ByteLong();
        return readInt(firstByte, fieldname);
    }
    
    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws IOException {
        if (checkForNullOrNeedToken(di, COMPACT_UUID))
            return null;
        long msl = readFixed8ByteLong();
        long lsl = readFixed8ByteLong();
        return new UUID(msl, lsl);
    }


    @Override
    public IOException enumExceptionConverter(IllegalArgumentException e) {
        return new IOException("Invalid enum token in " + currentClass + ": " + e.getMessage());
    }

    @Override
    public IOException customExceptionConverter(String msg, Exception e) {
        return new IOException("Adapter exception in " + currentClass + ": " + (e != null ? msg + e.toString() : msg));
    }


    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }

    
    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws IOException {
        XEnumDefinition spec = di.getBaseXEnum();
        String scannedToken = readString(di.getName(), di.getIsRequired() && !spec.getHasNullToken());
        if (scannedToken == null)
            return factory.getNullToken();
        T value = factory.getByToken(scannedToken);
        if (value == null) {
            throw new IOException("Invalid enum token " + scannedToken + " in " + currentClass + "." + di.getName());
        }
        return value;
    }


    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws IOException {
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
        }
        // now read mantissa. Either length  + digits, or an integer
        BigDecimal r;
        c = needToken();
        if (c == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), fieldname);
            
            byte [] mantissa = new byte [len];
            in.readFully(mantissa);
            r = new BigDecimal(new BigInteger(mantissa), scale);
        } else {
            c = readInt(c, fieldname);
            r = BigDecimal.valueOf(c, scale);
        }
        try {
            return BigDecimalTools.checkAndScale(r, di, -1, currentClass);
        } catch (MessageParserException e) {
            throw new IOException(e);
        }
    }


    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return Character.valueOf(readPrimitiveCharacter(di));
    }


    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == 0)
            return Boolean.FALSE;
        if (c == 1)
            return Boolean.TRUE;
        throw new IOException(String.format("Unexpected character: (expected BOOLEAN 0/1, got 0x%02x) in %s.%s", c, currentClass, di.getName()));
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return Double.valueOf(readPrimitiveDouble(di));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return Float.valueOf(readPrimitiveFloat(di));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return readLong(needToken(), di.getName());
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return readInt(needToken(), di.getName());
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return (short)readInt(needToken(), di.getName());
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return (byte)readInt(needToken(), di.getName());
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), di.getName());
            byte [] mantissa = new byte [len];
            in.readFully(mantissa);
            return new BigInteger(mantissa);
        } else {
            c = readInt(c, di.getName());
            return BigInteger.valueOf(c);
        }
    }

    private String readAscii(int len, String fieldname) throws IOException {
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = (char)(in.readUnsignedByte());
        return new String(data);
    }
    private String readUTF16(int len, String fieldname) throws IOException {
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = in.readChar();
        return new String(data);
    }
    
    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws IOException {
        return readString(di.getName(), di.getIsRequired());
    }
    
    protected String readString(String fieldname, boolean isRequired) throws IOException {
        if (checkForNull(fieldname, isRequired))
            return null;
        int len;
        String result;
        int c = needToken();
        if (c >= 0x20 && c < 0x80)
            return String.valueOf((char)c);  // single ASCII byte string
        if (c >= SHORT_ASCII_STRING && c <= SHORT_ASCII_STRING + 15) {
            len = c - SHORT_ASCII_STRING + 1;
            return readAscii(len, fieldname);
        }
        try {
            switch (c) {
            case EMPTY_FIELD:
                return EMPTY_STRING;
            case UNICODE_CHAR:
                return String.valueOf(in.readChar()); // single Unicode char string
            case ASCII_STRING:
                len = readInt(needToken(), fieldname);
                return readAscii(len, fieldname);
            case UTF8_STRING:
                len = readInt(needToken(), fieldname);
                byte [] tmp = new byte [len];
                in.readFully(tmp);
                result = new String(tmp, CHARSET_UTF8);
                return result;
            case UTF16_STRING:
                len = readInt(needToken(), fieldname);  // * 2 because we have 2 byte per character and any code below measures in bytes
                return readUTF16(len, fieldname);
            default:
                throw new IOException(String.format("expected STRING*, got 0x%02x in %s.%s", c, currentClass, fieldname));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException(String.format("Unsupported encoding encoding %02x in %s.%s", c, currentClass, fieldname));
        }
    }


    @Override
    public String readString(AlphanumericElementaryDataItem di) throws IOException {
        return readString(di.getName(), di.getIsRequired());
    }


    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return ByteArray.ZERO_BYTE_ARRAY;
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            return ByteArray.fromDataInput(in, len);
        default:
            throw new IOException(String.format("expected BINARY*, got 0x%02x in %s.%s", c, currentClass, di.getName()));
        }
    }


    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return EMPTY_BYTE_ARRAY;
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            byte [] data = new byte [len];
            in.readFully(data);
            return data;
        default:
            throw new IOException(String.format("expected BINARY*, got 0x%02x in %s.%s", c, currentClass, di.getName()));
        }
    }


    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws IOException {
        if (checkForNullOrNeedToken(di, COMPACT_DATE))
            return null;
        String fieldname = di.getName();
        int year = readInt(needToken(), fieldname);
        int month = readInt(needToken(), fieldname);
        int day = readInt(needToken(), fieldname);
        return new LocalDate(year, month, day);
    }


    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case COMPACT_TIME_MILLIS:
            return new LocalTime(readInt(needToken(), di.getName()), DateTimeZone.UTC);
        case COMPACT_TIME:
            return new LocalTime(readInt(needToken(), di.getName()) * 1000L, DateTimeZone.UTC);
        default:
            throw new IOException(String.format("expected COMPACT_TIME_*, got 0x%02x in %s.%s", c, currentClass, di.getName()));
        }
    }


    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        boolean fractional = false;
        int c = needToken();
        switch (c) {
        case COMPACT_DATETIME:
            break;
        case COMPACT_DATETIME_MILLIS:
            fractional = true;
            break;
        default:
            throw new IOException(String.format("expected DATETIME_*, got 0x%02x in %s.%s", c, currentClass, di.getName()));
        }
        String fieldname = di.getName();
        int year = readInt(needToken(), fieldname);
        int month = readInt(needToken(), fieldname);
        int day = readInt(needToken(), fieldname);
        int secondsOfDay = readInt(needToken(), fieldname);
        int millis = 0;
        if (fractional) {
            millis = secondsOfDay % 1000;
            secondsOfDay /= 1000;
        }
        return new LocalDateTime(year, month, day, secondsOfDay / 3600, (secondsOfDay % 3600) / 60, secondsOfDay % 60, millis);
    }


    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di))
            return null;
        return new Instant(readLong(needToken(), di.getName()));
    }


    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws IOException {
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
                throw new IOException(String.format("Invalid backreference for %s.%s: requested object %d of only %d available",
                        currentClass, fieldname, objectIndex, objects.size()));
            BonaPortable newObject = objects.get(objects.size() - 1 - objectIndex);  // 0 is the last one put in, 1 the one before last etc...
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new IOException(String.format("Bad class: got %s, expected %s for %s.%s, subclassing = %b",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), currentClass, fieldname, allowSubtypes));
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
                    throw new IOException(String.format("Bad class IDs %d / %d for %s.%s", factoryId, classId, currentClass, fieldname));
                classname = bclass.getPqon();
                newObject = bclass.newInstance();
            } else {
                if (c == OBJECT_BEGIN_BASE) {
                    if (di.getLowerBound() == null)
                        throw new IOException(String.format("Invalid base class reference for %s.%s", currentClass, fieldname));
                    classname = di.getLowerBound().getName();
                } else {
                    classname = readString(fieldname, false);
                    if (classname == null || classname.length() == 0) {
                        if (di.getLowerBound() == null)
                            throw new IOException(String.format("Invalid base class reference for %s.%s", currentClass, fieldname));
                        // the base class name is referred to, which is contained in the meta data
                        classname = di.getLowerBound().getName();
                    }
                    needToken(NULL_FIELD); // version not yet allowed
                }
                try {
                    newObject = BonaPortableFactory.createObject(classname);
                } catch (MessageParserException e) {
                    throw new IOException(e);  // wrap exception
                }
            }
            // System.out.println("Creating new obj " + classname + " gave me " + newObject);
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new IOException(String.format("Bad class: got %s, expected %s for %s.%s, subclassing = %b",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), currentClass, fieldname, allowSubtypes));
                }
            }
            // all good here. Parse the contents
            // if we use the cache, make the object known even before the contents has been parsed, because it may be referenced if the structure is cyclic
            if (useCache)
                objects.add(newObject);
            
            currentClass = classname;
            newObject.deserialize(this);
            skipNulls();
            needToken(OBJECT_TERMINATOR);
            currentClass = previousClass;
            return type.cast(newObject);
        } else {
            throw new IOException(String.format("expected OBJECT_START*, got 0x%02x in %s.%s", c, currentClass, fieldname));
        }
    }


    @Override
    public int parseMapStart(FieldDefinition di) throws IOException {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), MAP_BEGIN))
            return -1;
        return readInt(needToken(), di.getName());
    }


    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws IOException {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), ARRAY_BEGIN))
            return -1;
        return readInt(needToken(), di.getName());
    }


    @Override
    public void parseArrayEnd() throws IOException {
        needToken(COLLECTIONS_TERMINATOR);
    }


    @Override
    public BonaPortable readRecord() throws IOException {
        // there are no record start/end markers in this format
        return readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }


    @Override
    public List<BonaPortable> readTransmission() throws IOException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        for (;;) {
            // detect EOF
            try {
                int c = needToken();
                pushedBack = c;
                results.add(readRecord());
            } catch (EOFException e) {
                // done
                return results;
            }
        }
    }


    @Override
    public void eatParentSeparator() throws IOException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        needToken(PARENT_SEPARATOR);
    }
    
    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws IOException {
        int c = needToken();
        if (c == 0)
            return false;
        if (c == 1)
            return true;
        throw new IOException(String.format("Unexpected character: (expected BOOLEAN 0/1, got 0x%02x) in %s.%s", c, currentClass, di.getName()));
    }

    // default implementations for the next ones...
    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws IOException {
        int c = needToken();
        if (c >= 0x20 && c < 0x80)
            return (char)c;      // single byte char
        if (c != UNICODE_CHAR)
            throw new IOException(String.format("Unexpected character: (expected UNICODE_CHAR, got 0x%02x) in %s.%s", c, currentClass, di.getName()));
        return in.readChar();
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws IOException {
        int c = needToken();
        if (c == COMPACT_DOUBLE) {
            return Double.longBitsToDouble(readFixed8ByteLong());
        }
        // not a float, try upgrade of int to double (doubles of value 0 or 1 are explicitly written as int)
        return readInt(c, di.getName());
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws IOException {
        int c = needToken();
        if (c == COMPACT_FLOAT) {
            return Float.intBitsToFloat(readFixed4ByteInt());
        }
        // not a float, try upgrade of int to float (floats of value 0 or 1 are explicitly written as int)
        return readInt(c, di.getName());
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws IOException {
        return readLong(needToken(), di.getName());
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws IOException {
        return readInt(needToken(), di.getName());
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws IOException {
        return (short)readInt(needToken(), di.getName());
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws IOException {
        return (byte)readInt(needToken(), di.getName());
    }
}
