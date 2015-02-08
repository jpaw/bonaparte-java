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

import java.io.IOException;
import java.io.ObjectInput;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * The StringBuilderParser class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the deserialization for the internal format using the Externalizable interface.
 */

public final class ExternalizableParser extends ExternalizableConstants implements MessageParser<IOException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalizableParser.class);
    private final ObjectInput in;
    private String currentClass = "N/A";
    private boolean hasByte = false;
    private byte pushedBackByte = (byte)0;

    public ExternalizableParser(ObjectInput in) {
        this.in = in;
    }

    // entry called from generated objects:
    public static void deserialize(BonaPortable obj, ObjectInput _in) throws IOException, ClassNotFoundException {
        MessageParser<IOException> _p = new ExternalizableParser(_in);
        obj.deserialize(_p);
    }

    /**************************************************************************************************
     * Deserialization goes here
     * @throws IOException
     **************************************************************************************************/

    private byte nextByte() throws IOException {
        if (hasByte) {
            hasByte = false;
            return pushedBackByte;
        }
        return in.readByte();
    }

    private byte needToken() throws IOException {
        return nextByte();  // just a synonym
    }
    
    private void pushBack(byte b) {
        if (hasByte) {
            throw new RuntimeException("Duplicate pushback");
        }
        hasByte = true;
        pushedBackByte = b;
    }

    private void needToken(byte c) throws IOException {
        byte d = nextByte();
        if (c != d) {
            throw new IOException(String.format("Unexpected byte: expected 0x%02x, got 0x%02x in class %s",
                    (int)c, (int)d, currentClass));
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(FieldDefinition di) throws IOException {
        return checkForNull(di.getName(), di.getIsRequired());
    }
    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean isRequired) throws IOException {
        byte c = nextByte();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new IOException("Illegal explicit NULL in " + currentClass + "." + fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == ARRAY_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                pushBack(c);
                return true;
            } else {
                throw new IOException("Illegal implicit NULL in " + currentClass + "." + fieldname);
            }
        }
        pushBack(c);
        return false;
    }

    private void skipNulls() throws IOException {
        for (;;)  {
            byte c = nextByte();
            if (c != NULL_FIELD) {
                pushBack(c);
                break;
            }
        }
    }

    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        String fieldname = di.getName();
        BigDecimal r;
        byte c = nextByte();
        if ((c > FRAC_SCALE_0) && (c <= FRAC_SCALE_18)) {
            // read fractional part
            long fraction = readLongNoNull(fieldname);
            int scale = c-FRAC_SCALE_0;
            // combine with integral part and create scaled result
            r = BigDecimal.valueOf((powersOfTen[scale] * readLongNoNull(fieldname)) + fraction, scale);
        } else {
            pushBack(c);
            r = BigDecimal.valueOf(readLongNoNull(fieldname));
        }
        // now check precision, if required, convert!
        try {
            return BigDecimalTools.checkAndScale(r, di, -1, currentClass);
        } catch (MessageParserException a) {
            throw new IOException("Decimal number does not comply with specs: " + a.getStandardDescription() + " for " + currentClass + "." + fieldname);
        }
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws IOException {
        String tmp = readString(di.getName(), di.getIsRequired(), 1, false, false, true, true);
        if (tmp == null) {
            return null;
        }
        if (tmp.length() == 0) {
            throw new IOException("EMPTY CHAR in " + currentClass + "." + di.getName());
        }
        return tmp.charAt(0);
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws IOException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), false);
    }
    // readString does the job for Unicode as well as ASCII
    @Override
    public String readString(AlphanumericElementaryDataItem di) throws IOException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), true);
    }
    
    protected String readString(String fieldname, boolean isRequired, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws IOException {
        if (checkForNull(fieldname, isRequired)) {
            return null;
        }
        needToken(TEXT);
        String s = in.readUTF();
        if (doTrim) {
            // skip leading spaces
            s = s.trim();
        }
        if (doTruncate && (length > 0) && (s.length() > length)) {
            s = s.substring(0, length);
        }
        return s;
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        byte c = nextByte();
        if (c == INT_ZERO) {
            return Boolean.FALSE;
        } else if (c == INT_ONE) {
            return Boolean.TRUE;
        }
        throw new IOException(String.format("ILLEGAL BOOLEAN: found 0x%02x for %s.%s", (int)c, currentClass, di.getName()));
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        needToken(BINARY);
        assert !hasByte; // readInt() does not respect pushed back byte
        return ByteArray.read(in);
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        needToken(BINARY);
        assert !hasByte; // readInt() does not respect pushed back byte
        byte [] tmp = ByteArray.readBytes(in);
        int length = di.getLength();
        if ((length > 0) && (tmp.length > length)) {
            throw new IOException(String.format("byte buffer too long (found %d where only %d is allowed in %s.%s)",
                    tmp.length, length, currentClass, di.getName()));
        }
        return tmp;
    }

    private int readVarInt(String fieldname, int maxBits) throws IOException {
        byte c = nextByte();
        if ((c >= INT_MINUS_ONE) && (c <= NUMERIC_MAX)) {
            return c - INT_ZERO;
        }
        if (c == INT_ONEBYTE) {
            return in.readByte();
        }
        if ((c == INT_TWOBYTES) && (maxBits >= 16)) {
            return in.readShort();
        }
        if ((c == INT_FOURBYTES) && (maxBits >= 32)) {
            return in.readInt();
        }
        throw new IOException(String.format("No suitable integral token: %02x in %s.%s", c, currentClass, fieldname));
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        return (byte)readVarInt(di.getName(), 8);
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        return (short)readVarInt(di.getName(), 16);
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        return readVarInt(di.getName(), 32);
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        return readLongNoNull(di.getName());
    }

    private long readLongNoNull(String fieldname) throws IOException {
        byte c = nextByte();
        if (c == INT_EIGHTBYTES) {
            return in.readLong();
        }
        pushBack(c);
        return readVarInt(fieldname, 64);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        String fieldname = di.getName();
        int fractional = 0;
        byte c = nextByte();
        if ((c > FRAC_SCALE_0) && (c <= FRAC_SCALE_18)) {
            // read fractional part
            long fraction = readLongNoNull(fieldname);
            int scale = c-FRAC_SCALE_0;
            if (scale > 9) {
                fraction /= powersOfTen[scale - 9];
            } else if (scale < 9) {
                fraction *= powersOfTen[9 - scale];
            }
            fractional = (int)fraction;
        } else {
            pushBack(c);
        }
        int date = readVarInt(fieldname, 32);
        if ((date < 0) || (fractional < 0)) {
            throw new IOException(String.format("negative numbers found for date field: %d.%d in %s.%s",
                    date, fractional, currentClass, fieldname));
        }
        // set the date and time
        int day, month, year, hour, minute, second;
        year = date / 10000;
        month = (date %= 10000) / 100;
        day = date %= 100;
        if (di.getHhmmss()) {
            hour = fractional / 10000000;
            minute = (fractional %= 10000000) / 100000;
            second = (fractional %= 100000) / 1000;
        } else {
            hour = fractional / 3600000;
            minute = (fractional %= 3600000) / 60000;
            second = (fractional %= 60000) / 1000;
        }
        fractional %= 1000;
        // first checks
        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
                || (day > 31)) {
            throw new IOException(String.format("ILLEGAL DAY: found %d-%d-%d in %s.%s",
                    year, month, day, currentClass, fieldname));
        }
        if ((hour > 23) || (minute > 59) || (second > 59)) {
            throw new IOException(String.format("ILLEGAL TIME: found %d:%d:%d in %s.%s", hour, minute, second, currentClass, fieldname));
        }
        // now set the return value
        LocalDateTime result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = new LocalDateTime(year, month, day, hour, minute, second, fractional);
        } catch (Exception e) {
            throw new IOException(String.format("exception creating LocalDateTime for %d-%d-%d in %s.%s", year, month, day, currentClass, fieldname));
        }
        return result;
    }
    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        String fieldname = di.getName();
        int date = readVarInt(fieldname, 32);
        if (date < 0) {
            throw new IOException(String.format("negative numbers found for date field: %d in %s.%s",
                    date, currentClass, fieldname));
        }
        // set the date and time
        int day, month, year;
        year = date / 10000;
        month = (date %= 10000) / 100;
        day = date %= 100;
        // first checks
        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
                || (day > 31)) {
            throw new IOException(String.format("ILLEGAL DAY: found %d-%d-%d in %s.%s",
                    year, month, day, currentClass, fieldname));
        }
        // now set the return value
        LocalDate result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = new LocalDate(year, month, day);
        } catch (Exception e) {
            throw new IOException(String.format("exception creating LocalDate for %d-%d-%d in %s.%s", year, month, day, currentClass, fieldname));
        }
        return result;
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        int fractional = 0;
        byte c = nextByte();
        if ((c > FRAC_SCALE_0) && (c <= FRAC_SCALE_18)) {
            // read fractional part
            long fraction = readLongNoNull(di.getName());
            int scale = c-FRAC_SCALE_0;
            if (scale > 9) {
                fraction /= powersOfTen[scale - 9];
            } else if (scale < 9) {
                fraction *= powersOfTen[9 - scale];
            }
            fractional = (int)fraction;
        } else {
            pushBack(c);
        }
        // set the date and time
        int hour, minute, second;
        if (di.getHhmmss()) {
            hour = fractional / 10000000;
            minute = (fractional % 10000000) / 100000;
            second = (fractional % 100000) / 1000;
            fractional = (fractional % 1000) + 1000 * second + 60000 * minute + 3600000 * hour;
        } else {
            hour = fractional / 3600000;
            minute = (fractional % 3600000) / 60000;
            second = (fractional % 60000) / 1000;
        }
        // first checks
        if ((hour > 23) || (minute > 59) || (second > 59)) {
            throw new IOException(String.format("ILLEGAL TIME: found %d:%d:%d in %s.%s", hour, minute, second, currentClass, di.getName()));
        }
        return new LocalTime(fractional, DateTimeZone.UTC);
    }
    
    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        return new Instant(readLongNoNull(di.getName()));
    }
    
    @Override
    public int parseMapStart(FieldDefinition di) throws IOException {
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {  // check it separately in order to give a distinct error message
            if (di.getIsAggregateRequired())
                throw new IOException("ILLEGAL NULL Map in " + currentClass + "." + fieldname);
            return -1;
        }
        needToken(MAP_BEGIN);
        int foundIndexType = readVarInt(fieldname, 32);
        if (foundIndexType != di.getMapIndexType().ordinal()) {
            throw new IOException(String.format("WRONG_MAP_INDEX_TYPE: got %d, expected for %s.%s",
                    foundIndexType, di.getMapIndexType(), currentClass, fieldname));
        }
        int n = readVarInt(fieldname, 32);
        if ((n < 0) || (n > 1000000000)) {
            throw new IOException(String.format("ARRAY_SIZE_OUT_OF_BOUNDS: got %d entries (0x%x) for %s.%s",
                    n, n, currentClass, fieldname));
        }
        return n;
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfChild) throws IOException {
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {  // check it separately in order to give a distinct error message
            if (di.getIsAggregateRequired())
                throw new IOException("ILLEGAL NULL List, Set or Array in " + currentClass + "." + fieldname);
            return -1;
        }
        needToken(ARRAY_BEGIN);
        int n = readVarInt(fieldname, 32);
        if ((n < 0) || (n > 1000000000)) {
            throw new IOException(String.format("ARRAY_SIZE_OUT_OF_BOUNDS: got %d entries (0x%x) for %s.%s",
                    n, n, currentClass, fieldname));
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws IOException {
        needToken(ARRAY_TERMINATOR);

    }

    @Override
    public BonaPortable readRecord() throws IOException {
        BonaPortable result;
        needToken(RECORD_BEGIN);
        needToken(NULL_FIELD); // version no
        result = readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
        needToken(RECORD_TERMINATOR);
        return result;
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        // TODO: accept other numeric types as well & perform conversion
        needToken(BINARY_FLOAT);
        return in.readFloat();
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        needToken(BINARY_DOUBLE);
        return in.readDouble();
    }

    @Override
    public void eatParentSeparator() throws IOException {
        eatObjectOrParentSeparator(PARENT_SEPARATOR);
    }       
        
    public void eatObjectTerminator() throws IOException {
        eatObjectOrParentSeparator(OBJECT_TERMINATOR);
    }
    
    protected void eatObjectOrParentSeparator(byte which) throws IOException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        byte z = needToken();
        if (z == which)
            return;   // all good
        
        // we have extra data and it is not null. Now the behavior depends on a parser setting
        ParseSkipNonNulls mySetting = getSkipNonNullsBehavior();
        switch (mySetting) {
        case ERROR:
            throw new IOException("Extra fields found after object end. Outdated parser? " + currentClass);  
        case WARN:
            LOGGER.warn("Extra fields found after object end, parsing class {}. Parser outdated?", currentClass);
            // fall through
        case IGNORE:
            // skip bytes until we are at end of record (bad!) (thrown by needToken()) or find the terminator
            skipUntilNext(which);
        }
    }
    
    protected void skipUntilNext(byte which) throws IOException {
        byte c;
        while ((c = needToken()) != which) {
            if (c == OBJECT_BEGIN) {
                // skip nested object!
                skipUntilNext(OBJECT_TERMINATOR);
            }
        }
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        needToken(BINARY);
        assert !hasByte; // readInt() does not respect pushed back byte
        byte [] tmp = ByteArray.readBytes(in);
        return new BigInteger(tmp);
    }

    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws IOException {
        if (checkForNull(di)) {
            return null;
        }
        boolean allowSubtypes = di.getAllowSubclasses();
        String fieldname = di.getName();
        String previousClass = currentClass;
        needToken(OBJECT_BEGIN);  // version not yet allowed
        BonaPortable newObject;
        if (nestedObjectsInternally) {
            String classname = in.readUTF();
            // String revision = readAscii(true, 0, false, false);
            // long serialUID = in.readLong();  // version not yet allowed
            needToken(NULL_FIELD);  // version not yet allowed

            try {
                newObject = BonaPortableFactory.createObject(classname);
            } catch (MessageParserException e) {
                throw new IOException(e);
            }
            // system.out.println("Creating new obj " + classname + " gave me " + newObject);
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new IOException(String.format("BAD_CLASS: got %s, expected %s, subclassing = %b in %s.%s",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), allowSubtypes,
                            currentClass, fieldname));
                }
            }
            // all good here. Parse the contents
            currentClass = classname;
            newObject.deserialize(this);
            eatObjectTerminator();
        } else {
            try {
                newObject = (BonaPortable)in.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
            // the following test happens AFTER the class has been read. With the internal variant, it is done BEFORE any fields are parsed.
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new IOException(String.format("BAD_CLASS: got %s, expected %s, subclassing = %b in %s.%s",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), allowSubtypes,
                            currentClass, fieldname));
                }
            }
        }
        currentClass = previousClass;       // pop class name
        return type.cast(newObject);
    }

    @Override
    public List<BonaPortable> readTransmission() throws IOException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        byte c = nextByte();
        if (c == TRANSMISSION_BEGIN) {
            needToken(NULL_FIELD);  // version
            // TODO: parse extensions here
            while ((c = nextByte()) != TRANSMISSION_TERMINATOR) {
                // System.out.println("transmission loop: char is " + c);
                pushBack(c); // push back object def
                results.add(readRecord());
            }
            // when here, last char was transmission terminator
            // optionally eat the last one as well?
        } else if (c == RECORD_BEGIN /* || c == EXTENSION_BEGIN */) {
            // allow single record as a special case
            // TODO: parse extensions here


            pushBack(c);
            results.add(readRecord());
        } else {
            throw new IOException(String.format("BAD_TRANSMISSION_START: got 0x%02x in %s", (int)c, currentClass));
        }
        // expect that the transmission ends here! TODO: exception if not
        return results;
    }


    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws IOException {
        String tmp = readString(di.getName(), di.getIsRequired(), 36, false, false, false, false);
        if (tmp == null) {
            return null;
        }
        try {
            return UUID.fromString(tmp);
        } catch (IllegalArgumentException e) {
            throw new IOException("UUID in " + currentClass);
        }
    }

    @Override
    public IOException enumExceptionConverter(IllegalArgumentException e) {
        return new IOException(e);
    }

    @Override
    public IOException customExceptionConverter(String msg, Exception e) {
        return new IOException("Cannot construct custom object in " + currentClass + ": " + msg + (e != null ? e.toString() : ""));
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws IOException {
        XEnumDefinition spec = di.getBaseXEnum();
        String scannedToken = readString(di.getName(), di.getIsRequired() && !spec.getHasNullToken(), spec.getMaxTokenLength(), true, false, false, true);
        if (scannedToken == null)
            return factory.getNullToken();
        T value = factory.getByToken(scannedToken);
        if (value == null) {
            throw new IOException(String.format("Invalid enum token %s for field %s.%s", scannedToken, currentClass, di.getName()));
        }
        return value;
    }

    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws IOException {
        int c = needToken();
        if (c == INT_ZERO)
            return false;
        if (c == INT_ONE)
            return true;
        throw new IOException(String.format("Unexpected character: (expected BOOLEAN 0/1, got 0x%02x) in %s.%s", c, currentClass, di.getName()));
    }

    // default implementations for the next ones...
    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws IOException {
        String tmp = readString(di.getName(), true, 1, false, false, true, true);
        if (tmp.length() == 0) {
            throw new IOException("EMPTY CHAR in " + currentClass + "." + di.getName());
        }
        return tmp.charAt(0);
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws IOException {
        needToken(BINARY_DOUBLE);
        return in.readDouble();
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws IOException {
        needToken(BINARY_FLOAT);
        return in.readFloat();
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws IOException {
        return readLongNoNull(di.getName());
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws IOException {
        return readVarInt(di.getName(), 32);
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws IOException {
        return (short)readVarInt(di.getName(), 16);
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws IOException {
        return (byte)readVarInt(di.getName(), 8);
    }
}

