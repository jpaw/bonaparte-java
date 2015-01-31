/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.bonaparte.core;

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

/**
 * The CompactByteArrayParser class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implementation of the MessageParser for the binary compact protocol, using byte arrays.
 */

public class CompactByteArrayParser extends CompactConstants implements MessageParser<MessageParserException> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(CompactByteArrayParser.class);
    private static final byte [] EMPTY_BYTE_ARRAY = new byte [0];
    private static final String EMPTY_STRING = "";
    
    private int parseIndex;
    private int messageLength;
    private byte [] inputdata;
    private String currentClass;
    private final boolean useCache = true;
    private List<BonaPortable> objects;
    
    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static <T extends BonaPortable> T unmarshal(byte [] x, ObjectReference di, Class<T> expectedClass) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new CompactByteArrayParser(x, 0, -1).readObject(di, expectedClass);
    }

    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src, int offset, int length) {
        inputdata = src;
        parseIndex = offset;
        messageLength = length;
        if (useCache)
            objects.clear();
    }
    
    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src) {
        inputdata = src;
        parseIndex = 0;
        messageLength = src.length;
        if (useCache)
            objects.clear();
    }
    
    /** Create a processor for parsing a buffer. */
    public CompactByteArrayParser(byte [] buffer, int offset, int length) {
        inputdata = buffer;
        parseIndex = offset;
        messageLength = length < 0 ? inputdata.length : length; // -1 means full array size
        currentClass = "N/A";
        if (useCache)
            objects = new ArrayList<BonaPortable>(60);
    }

    /**************************************************************************************************
     * Deserialization goes here. Code below does not use the ByteBuilder class,
     * but reads from the byte[] directly
     **************************************************************************************************/

    private void require(int length) throws MessageParserException {
        if (parseIndex + length > messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END, null, parseIndex, currentClass);
        }
    }
    
    private int needToken() throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END, null, parseIndex, currentClass);
        }
        return inputdata[parseIndex++] & 0xff;
    }

    private void needToken(int c) throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END,
                    String.format("(expected 0x%02x)", c), parseIndex, currentClass);
        }
        int d = inputdata[parseIndex++] & 0xff;
        if (c != d) {
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected 0x%02x, got 0x%02x)", c, d), parseIndex, currentClass);
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNullOrNeedToken(FieldDefinition di, int token) throws MessageParserException {
        return checkForNullOrNeedToken(di.getName(), di.getIsRequired(), token);
    }
    // check for Null called for field members inside a class
    private boolean checkForNullOrNeedToken(String fieldname, boolean isRequired, int token) throws MessageParserException {
        int c = needToken();
        if (c == token)
            return false;
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == COLLECTIONS_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                --parseIndex;
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                String.format("(expected 0x%02x, got 0x%02x)", token, c), parseIndex, currentClass);
    }
    
    // check for Null called for field members inside a class
    private boolean checkForNull(FieldDefinition di) throws MessageParserException {
        return checkForNull(di.getName(), di.getIsRequired());
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean isRequired) throws MessageParserException {
        int c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == COLLECTIONS_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                --parseIndex;
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        --parseIndex;
        return false;
    }
    
    // upon entry, we know that firstByte is not null (0xa0)
    private int readInt(int firstByte, String fieldname) throws MessageParserException {
        if (firstByte < 0xa0) {
            // 1 positive byte numbers 
            if (firstByte <= 31)
                return firstByte;
            if (firstByte >= 0x80)
                return firstByte - 0x60;  // 0x20..0x3f
            throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
        }
        if (firstByte <= 0xd0) {
            if (firstByte <= 0xaa)
                return 0xa0 - firstByte;  // -1 .. -10
            if (firstByte < 0xc0)
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
            // 2 byte number 0...2047
            return needToken() + ((firstByte & 0x0f) << 8);
        }
        switch (firstByte) {
        case INT_2BYTE:
            short n = (short)(needToken() << 8);
            n += needToken();
            return n;
        case INT_3BYTE:
            require(3);
            int nn = (inputdata[parseIndex++] & 0xff) << 16;
            nn += (inputdata[parseIndex++] & 0xff) << 8;
            nn += inputdata[parseIndex++] & 0xff;
            if ((nn & 0x800000) != 0)
                nn |= 0xff << 24;   // sign-extend
            return nn;
        case INT_4BYTE:
            return readFixed4ByteInt();
        default:
            throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
        }
    }

    private int readFixed4ByteInt() throws MessageParserException {
        require(4);
        int nn = (inputdata[parseIndex++] & 0xff) << 24;
        nn += (inputdata[parseIndex++] & 0xff) << 16;
        nn += (inputdata[parseIndex++] & 0xff) << 8;
        nn += inputdata[parseIndex++] & 0xff;
        return nn;
    }
    
    private long readFixed8ByteLong() throws MessageParserException {
        require(8);
        int nn1 = (inputdata[parseIndex++] & 0xff) << 24;
        nn1 += (inputdata[parseIndex++] & 0xff) << 16;
        nn1 += (inputdata[parseIndex++] & 0xff) << 8;
        nn1 += inputdata[parseIndex++] & 0xff;
        int nn2 = (inputdata[parseIndex++] & 0xff) << 24;
        nn2 += (inputdata[parseIndex++] & 0xff) << 16;
        nn2 += (inputdata[parseIndex++] & 0xff) << 8;
        nn2 += inputdata[parseIndex++] & 0xff;
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }
    private long readLong(int firstByte, String fieldname) throws MessageParserException {
        if (firstByte != INT_8BYTE)
            return readInt(firstByte, fieldname);
        return readFixed8ByteLong();
    }
    
    private void skipNulls() {
        while (parseIndex < messageLength) {
            int c = inputdata[parseIndex] & 0xff;
            if (c != NULL_FIELD) {
                break;
            }
            // skip trailing NULL objects
            ++parseIndex;
        }
    }


    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        if (checkForNullOrNeedToken(di, COMPACT_UUID))
            return null;
        long msl = readFixed8ByteLong();
        long lsl = readFixed8ByteLong();
        return new UUID(msl, lsl);
    }


    @Override
    public MessageParserException enumExceptionConverter(IllegalArgumentException e) {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage(), parseIndex, currentClass);
    }

    @Override
    public MessageParserException customExceptionConverter(String msg, Exception e) {
        return new MessageParserException(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg, parseIndex, currentClass);
    }


    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }


    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
        XEnumDefinition spec = di.getBaseXEnum();
        String scannedToken = readString(di.getName(), di.getIsRequired() && !spec.getHasNullToken());
        if (scannedToken == null)
            return factory.getNullToken();
        T value = factory.getByToken(scannedToken);
        if (value == null) {
            throw new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, scannedToken, parseIndex, currentClass);
        }
        return value;
    }


    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
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
            require(len);
            byte [] mantissa = new byte [len];
            System.arraycopy(inputdata, parseIndex, mantissa, 0, len);
            parseIndex += len;
            r = new BigDecimal(new BigInteger(mantissa), scale);
        } else {
            c = readInt(c, fieldname);
            r = BigDecimal.valueOf(c, scale);
        }
        return BigDecimalTools.checkAndScale(r, di, parseIndex, currentClass);
    }


    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c >= 0x20 && c < 0x80)
            return Character.valueOf((char)c);      // single byte char
        if (c != UNICODE_CHAR)
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected UNICODE_CHAR, got 0x%02x)", c), parseIndex, currentClass);
        require(2);
        char cc = (char)(((inputdata[parseIndex] & 0xff) << 8) | (inputdata[parseIndex+1] & 0xff));
        parseIndex += 2;
        return cc;
    }


    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == 0)
            return Boolean.FALSE;
        if (c == 1)
            return Boolean.TRUE;
        throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                String.format("(expected BOOLEAN 0/1, got 0x%02x)", c), parseIndex, currentClass);
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == COMPACT_DOUBLE) {
            return Double.longBitsToDouble(readFixed8ByteLong());
        }
        // not a float, try upgrade of int to double (doubles of value 0 or 1 are explicitly written as int)
        return (double)readInt(c, di.getName());
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == COMPACT_FLOAT) {
            return Float.intBitsToFloat(readFixed4ByteInt());
        }
        // not a float, try upgrade of int to float (floats of value 0 or 1 are explicitly written as int)
        return (float)readInt(c, di.getName());
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        return readLong(needToken(), di.getName());
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        return readInt(needToken(), di.getName());
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        return (short)readInt(needToken(), di.getName());
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        return (byte)readInt(needToken(), di.getName());
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        if (c == COMPACT_BIGINTEGER) {
            // length and mantissa
            int len = readInt(needToken(), di.getName());
            require(len);
            byte [] mantissa = new byte [len];
            System.arraycopy(inputdata, parseIndex, mantissa, 0, len);
            parseIndex += len;
            return new BigInteger(mantissa);
        } else {
            c = readInt(c, di.getName());
            return BigInteger.valueOf(c);
        }
    }

    private String readAscii(int len, String fieldname) throws MessageParserException {
        require(len);
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = (char)(inputdata[parseIndex++] & 0xff);
        return new String(data);
    }
    
    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired());
    }
    
    protected String readString(String fieldname, boolean isRequired) throws MessageParserException {
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
                require(2);
                char cc = (char)(((inputdata[parseIndex] & 0xff) << 8) | (inputdata[parseIndex+1] & 0xff));
                parseIndex += 2;
                return String.valueOf(cc); // single Unicode char string
            case ASCII_STRING:
                len = readInt(needToken(), fieldname);
                // return readAscii(len, di.getName());
                require(len);
                result = new String(inputdata, parseIndex, len, CHARSET_ASCII);
                parseIndex += len;
                return result;
            case UTF8_STRING:
                len = readInt(needToken(), fieldname);
                require(len);
                result = new String(inputdata, parseIndex, len, CHARSET_UTF8);
                parseIndex += len;
                return result;
            case UTF16_STRING:
                len = 2 * readInt(needToken(), fieldname);  // * 2 because we have 2 byte per character and any code below measures in bytes
                require(len);
                result = new String(inputdata, parseIndex, len, CHARSET_UTF16);
                parseIndex += len;
                return result;
            default:
                throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected STRING*, got 0x%02x)", c), parseIndex,
                        currentClass);
            }
        } catch (UnsupportedEncodingException e) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_ASCII, String.format("(encoding %02x)", c), parseIndex, currentClass);
        }
    }


    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired());
    }


    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return ByteArray.ZERO_BYTE_ARRAY;
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            ByteArray result = new ByteArray(inputdata, parseIndex, len);
            parseIndex += len;
            return result;
        default:
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected BINARY*, got 0x%02x)", c), parseIndex, currentClass);
        }
    }


    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case EMPTY_FIELD:
            return EMPTY_BYTE_ARRAY;
        case COMPACT_BINARY:
            int len = readInt(needToken(), di.getName());
            byte [] data = new byte [len];
            System.arraycopy(inputdata, parseIndex, data, 0, len);
            parseIndex += len;
            return data;
        default:
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected BINARY*, got 0x%02x)", c), parseIndex, currentClass);
        }
    }


    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNullOrNeedToken(di, COMPACT_DATE))
            return null;
        String fieldname = di.getName();
        int year = readInt(needToken(), fieldname);
        int month = readInt(needToken(), fieldname);
        int day = readInt(needToken(), fieldname);
        return new LocalDate(year, month, day);
    }


    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di))
            return null;
        int c = needToken();
        switch (c) {
        case COMPACT_TIME_MILLIS:
            return new LocalTime(readInt(needToken(), di.getName()), DateTimeZone.UTC);
        case COMPACT_TIME:
            return new LocalTime(readInt(needToken(), di.getName()) * 1000L, DateTimeZone.UTC);
        default:
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected COMPACT_TIME_*, got 0x%02x)", c), parseIndex, currentClass);
        }
    }


    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
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
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected COMPACT_DATETIME_*, got 0x%02x)", c), parseIndex, currentClass);
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
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNullOrNeedToken(di, INT_8BYTE))
            return null;
        return new Instant(readFixed8ByteLong());
    }


    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws MessageParserException {
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
                throw new MessageParserException(MessageParserException.INVALID_BACKREFERENCE, String.format(
                        "at %s: requested object %d of only %d available", fieldname, objectIndex, objects.size()),
                        parseIndex, currentClass);
            BonaPortable newObject = objects.get(objects.size() - 1 - objectIndex);  // 0 is the last one put in, 1 the one before last etc...
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new MessageParserException(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
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
                    throw new MessageParserException(MessageParserException.BAD_CLASS_IDS, factoryId + "/" + classId, parseIndex, currentClass);
                classname = bclass.getPqon();
                newObject = bclass.newInstance();
            } else {
                if (c == OBJECT_BEGIN_BASE) {
                    if (di.getLowerBound() == null)
                        throw new MessageParserException(MessageParserException.INVALID_BASE_CLASS_REFERENCE, "", parseIndex, currentClass);
                    classname = di.getLowerBound().getName();
                } else {
                    classname = readString(fieldname, false);
                    if (classname == null || classname.length() == 0) {
                        if (di.getLowerBound() == null)
                            throw new MessageParserException(MessageParserException.INVALID_BASE_CLASS_REFERENCE, "", parseIndex, currentClass);
                        // the base class name is referred to, which is contained in the meta data
                        classname = di.getLowerBound().getName();
                    }
                    needToken(NULL_FIELD); // version not yet allowed
                }
                newObject = BonaPortableFactory.createObject(classname);
            }
            // System.out.println("Creating new obj " + classname + " gave me " + newObject);
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new MessageParserException(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
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
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected OBJECT_START*, got 0x%02x)", c), parseIndex, currentClass);
        }
    }


    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), MAP_BEGIN))
            return -1;
        return readInt(needToken(), di.getName());
    }


    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws MessageParserException {
        if (checkForNullOrNeedToken(di.getName(), di.getIsAggregateRequired(), ARRAY_BEGIN))
            return -1;
        return readInt(needToken(), di.getName());
    }


    @Override
    public void parseArrayEnd() throws MessageParserException {
        needToken(COLLECTIONS_TERMINATOR);
    }


    @Override
    public BonaPortable readRecord() throws MessageParserException {
        // there are no record start/end markers in this format
        return readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }


    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        while (parseIndex < messageLength)
            results.add(readRecord());
        return results;
    }


    @Override
    public void eatParentSeparator() throws MessageParserException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        needToken(PARENT_SEPARATOR);
    }
}
