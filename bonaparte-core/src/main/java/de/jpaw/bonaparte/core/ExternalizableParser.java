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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.util.ByteArray;
import de.jpaw.util.EnumException;
/**
 * The StringBuilderParser class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the deserialization for the internal format using the Externalizable interface.
 */

public final class ExternalizableParser extends ExternalizableConstants implements MessageParser<IOException> {
    private final ObjectInput in;
    private String currentClass = "N/A";
    private boolean hasByte = false;
    private byte pushedBackByte = (byte)0;

    public ExternalizableParser(ObjectInput in) {
        this.in = in;
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

    private void pushBack(byte b) {
        if (hasByte) {
            throw new RuntimeException("Duplicate pushback");
        }
        hasByte = true;
        pushedBackByte = b;
    }

    private void needByte(byte c) throws IOException {
        byte d = nextByte();
        if (c != d) {
            throw new IOException(String.format("Unexpected byte: expected 0x%02x, got 0x%02x in class %s",
                    (int)c, (int)d, currentClass));
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean allowNull) throws IOException {
        byte c = nextByte();
        if (c == NULL_FIELD) {
            if (allowNull) {
                return true;
            } else {
                throw new IOException("ILLEGAL EXPLICIT NULL in " + currentClass + "." + fieldname);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == ARRAY_TERMINATOR)) {
            if (allowNull) {
                // uneat it
                pushBack(c);
                return true;
            } else {
                throw new IOException("ILLEGAL IMPLICIT NULL in " + currentClass + "." + fieldname);
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
    public BigDecimal readBigDecimal(String fieldname, boolean allowNull, int length, int decimals, boolean isSigned, boolean rounding, boolean autoScale) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
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
            if (r.scale() > decimals)
                r = r.setScale(decimals, rounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
            if (autoScale && r.scale() < decimals)  // round for smaller as well!
                r = r.setScale(decimals, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException a) {
            throw new IOException("Too many decimals, would need to round for " + currentClass + "." + fieldname);
        }
        return r;
    }

    @Override
    public Character readCharacter(String fieldname, boolean allowNull) throws IOException {
        String tmp = readString(fieldname, allowNull, 1, false, false, true, true);
        if (tmp == null) {
            return null;
        }
        if (tmp.length() == 0) {
            throw new IOException("EMPTY CHAR in " + currentClass + "." + fieldname);
        }
        return tmp.charAt(0);
    }

    // readString does the job for Unicode as well as ASCII, but only used for Unicode (have an optimized version for ASCII)
    @Override
    public String readString(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        needByte(TEXT);
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

    // specialized version without charset conversion
    @Override
    public String readAscii(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws IOException {
        return readString(fieldname, allowNull, length, doTrim, doTruncate, false, false);
    }

    @Override
    public Boolean readBoolean(String fieldname, boolean allowNull) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        byte c = nextByte();
        if (c == INT_ZERO) {
            return false;
        } else if (c == INT_ONE) {
            return true;
        }
        throw new IOException(String.format("ILLEGAL BOOLEAN: found 0x%02x for %s.%s", (int)c, currentClass, fieldname));
    }

    @Override
    public ByteArray readByteArray(String fieldname, boolean allowNull, int length) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        needByte(BINARY);
        assert !hasByte; // readInt() does not respect pushed back byte
        return ByteArray.read(in);
    }

    @Override
    public byte[] readRaw(String fieldname, boolean allowNull, int length) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        needByte(BINARY);
        assert !hasByte; // readInt() does not respect pushed back byte
        byte [] tmp = ByteArray.readBytes(in);
        if ((length > 0) && (tmp.length > length)) {
            throw new IOException(String.format("byte buffer too long (found %d where only %d is allowed in %s.%s)",
                    tmp.length, length, currentClass, fieldname));
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
    public Byte readByte(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        return (byte)readVarInt(fieldname, 8);
    }

    @Override
    public Short readShort(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        return (short)readVarInt(fieldname, 16);
    }

    @Override
    public Integer readInteger(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        return readVarInt(fieldname, 32);
    }

    @Override
    public Long readLong(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        return readLongNoNull(fieldname);
    }

    private long readLongNoNull(String fieldname) throws IOException {
        byte c = nextByte();
        if (c == INT_EIGHTBYTES) {
            return in.readLong();
        }
        pushBack(c);
        return (long)readVarInt(fieldname, 64);
    }

    @Override
    public Calendar readCalendar(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
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
        if (hhmmss) {
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
        GregorianCalendar result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = new GregorianCalendar(year, month - 1, day, hour, minute,
                    second);
        } catch (Exception e) {
            throw new IOException(String.format("exception creating GregorianCalendar for %d-%d-%d in %s.%s", year, month, day, currentClass, fieldname));
        }
        result.set(Calendar.MILLISECOND, fractional);
        return result;
    }

    @Override
    public LocalDateTime readDayTime(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
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
        if (hhmmss) {
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
    public LocalDate readDay(String fieldname, boolean allowNull) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
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
    public int parseMapStart(String fieldname, boolean allowNull, int indexID) throws IOException {
        if (checkForNull(fieldname, true)) {  // check it separately in order to give a distinct error message
            if (!allowNull)
                throw new IOException("ILLEGAL NULL Map in " + currentClass + "." + fieldname);
            return -1;
        }
        needByte(MAP_BEGIN);
        int foundIndexType = readVarInt(fieldname, 32);
        if (foundIndexType != indexID) {
            throw new IOException(String.format("WRONG_MAP_INDEX_TYPE: got %d, expected for %s.%s",
                    foundIndexType, indexID, currentClass, fieldname));
        }
        int n = readVarInt(fieldname, 32);
        if ((n < 0) || (n > 1000000000)) {
            throw new IOException(String.format("ARRAY_SIZE_OUT_OF_BOUNDS: got %d entries (0x%x) for %s.%s",
                    n, n, currentClass, fieldname));
        }
        return n;
    }

    @Override
    public int parseArrayStart(String fieldname, boolean allowNull, int max, int sizeOfChild) throws IOException {
        if (checkForNull(fieldname, true)) {  // check it separately in order to give a distinct error message
            if (!allowNull)
                throw new IOException("ILLEGAL NULL List, Set or Array in " + currentClass + "." + fieldname);
            return -1;
        }
        needByte(ARRAY_BEGIN);
        int n = readVarInt(fieldname, 32);
        if ((n < 0) || (n > 1000000000)) {
            throw new IOException(String.format("ARRAY_SIZE_OUT_OF_BOUNDS: got %d entries (0x%x) for %s.%s",
                    n, n, currentClass, fieldname));
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws IOException {
        needByte(ARRAY_TERMINATOR);

    }

    @Override
    public BonaPortable readRecord() throws IOException {
        BonaPortable result;
        needByte(RECORD_BEGIN);
        needByte(NULL_FIELD); // version no
        result = readObject(GENERIC_RECORD, BonaPortable.class, false, true);
        needByte(RECORD_TERMINATOR);
        return result;
    }

    @Override
    public Float readFloat(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        // TODO: accept other numeric types as well & perform conversion
        needByte(BINARY_FLOAT);
        return in.readFloat();
    }

    @Override
    public Double readDouble(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        needByte(BINARY_DOUBLE);
        return in.readDouble();
    }

    @Override
    public void eatParentSeparator() throws IOException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        // TODO: also skip them if not blank, but corresponding flag is set
        needByte(PARENT_SEPARATOR);
    }

    @Override
    public Integer readNumber(String fieldname, boolean allowNull, int length, boolean isSigned) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        return Integer.valueOf(readVarInt(fieldname, 32));
    }

    @Override
    public BonaPortable readObject(String fieldname, Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws IOException {
        if (checkForNull(fieldname, allowNull)) {
            return null;
        }
        String previousClass = currentClass;
        needByte(OBJECT_BEGIN);  // version not yet allowed
        BonaPortable newObject;
        if (nestedObjectsInternally) {
            String classname = in.readUTF();
            // String revision = readAscii(true, 0, false, false);
            // long serialUID = in.readLong();  // version not yet allowed
            needByte(NULL_FIELD);  // version not yet allowed

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
        return newObject;
    }

    @Override
    public List<BonaPortable> readTransmission() throws IOException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        byte c = nextByte();
        if (c == TRANSMISSION_BEGIN) {
            needByte(NULL_FIELD);  // version
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
    public UUID readUUID(String fieldname, boolean allowNull) throws IOException {
        String tmp = readAscii(fieldname, allowNull, 36, true, false);
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
    public IOException enumExceptionConverter(EnumException e) {
        return new IOException(e);
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }
}

