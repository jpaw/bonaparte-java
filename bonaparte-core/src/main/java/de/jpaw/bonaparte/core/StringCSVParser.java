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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
import de.jpaw.util.EnumException;
/**
 * The StringCSVParser class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the deserialization of fixed width an quote-less CSV formats.
 *          Right now, only limited subsets are implemented. Especially date / time parsing is very limited.
 */

public final class StringCSVParser extends StringBuilderConstants implements MessageParser<MessageParserException> {
    protected final CSVConfiguration cfg;
    private final boolean fixedLength;
    private final String work;
    private final int lengthOfBoolean;
    private int parseIndex;  // for parser
    private int messageLength;  // for parser
    private String currentClass;
    protected final DateTimeFormatter dayFormat;            // day without time (Joda)
    protected final DateTimeFormatter timestampFormat;      // day and time on second precision (Joda)
    protected final DateTimeFormatter timestamp3Format;     // day and time on millisecond precision (Joda)
    protected final DateFormat calendarFormat;              // Java's mutable Calendar. Use with caution (or better, don't use at all)
    protected final int dayFormatLength;            // day without time (Joda)
    protected final int timestampFormatLength;      // day and time on second precision (Joda)
    protected final int timestamp3FormatLength;     // day and time on millisecond precision (Joda)
    protected final int calendarFormatLength;              // Java's mutable Calendar. Use with caution (or better, don't use at all)


    public StringCSVParser(CSVConfiguration cfg, String work) {
        // strip CR/LF from input, if existing
        messageLength = work.length();
        if (messageLength > 0 && work.charAt(messageLength-1) == '\n') {
            work = work.substring(0, --messageLength);
        }
        if (messageLength > 0 && work.charAt(messageLength-1) == '\r') {
            work = work.substring(0, --messageLength);
        }
        this.cfg = cfg;
        this.work = work;
        this.lengthOfBoolean = cfg.booleanFalse.length() > cfg.booleanTrue.length() ? cfg.booleanFalse.length() : cfg.booleanTrue.length();
        this.dayFormat = cfg.determineDayFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timestampFormat = cfg.determineTimestampFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timestamp3Format = cfg.determineTimestamp3Formatter().withLocale(cfg.locale).withZoneUTC();
        this.calendarFormat = cfg.determineCalendarFormat();
        this.calendarFormat.setCalendar(Calendar.getInstance(cfg.locale));
        this.dayFormatLength = cfg.customDayFormat == null ? 8 : cfg.customDayFormat.length();
        this.timestampFormatLength = cfg.customTimestampFormat == null ? 14 : cfg.customTimestampFormat.length();
        this.timestamp3FormatLength = cfg.customTimestampWithMsFormat == null ? 17 : cfg.customTimestampWithMsFormat.length();
        this.calendarFormatLength = cfg.customCalendarFormat == null ? 14 : cfg.customCalendarFormat.length();
        fixedLength = cfg.separator.length() == 0;
        parseIndex = 0;  // for parser
        currentClass = "N/A";
    }

    /**************************************************************************************************
     * Deserialization goes here
     **************************************************************************************************/
    

    private String getField(String fieldname, boolean allowNull, int length) throws MessageParserException {
        String result = null;
        if (fixedLength) {
            if (parseIndex == messageLength) {
            	// implicit null at field boundary: fall through
            	length = 0;
            } else if (parseIndex + length <= messageLength) {
                // have sufficient length
                result = work.substring(parseIndex, parseIndex+length);
                parseIndex += length;
            } else {
            	// record ends within a field!
                // insufficient length: throw an exception if incomplete field, or maybe allow an implicit null
            	// commented out, allow for that and fill with blanks
//                throw new MessageParserException(MessageParserException.PREMATURE_END,
//                    String.format("(remaining length %d, expected %d)", messageLength - parseIndex, length), parseIndex, currentClass);
            	// adjust length instead
            	length = messageLength - parseIndex;
                result = work.substring(parseIndex, messageLength);
            	parseIndex = messageLength;
            }            	
            // implicitly strip trailing spaces
            while (length > 0 && result.charAt(length - 1) == ' ') {
                --length;
            }
            result = length == 0 ? null : result.substring(0, length);
            // ending here, implicit end
            // fall through with null
        } else {
            int index = work.indexOf(cfg.separator, parseIndex);
            if (index < 0) {
                result = work.substring(parseIndex);
                parseIndex = messageLength;
            } else {
                result = work.substring(parseIndex, index);
                parseIndex = index + 1; // skip field and separator
            }
            if (result.length() == 0)
                result = null;
        }
        if (result == null && !allowNull)
            throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
        return result;
    }

    @Override
    public Boolean readBoolean(String fieldname, boolean allowNull) throws MessageParserException {
        String token = getField(fieldname, allowNull, lengthOfBoolean);
        if (token == null)
            return null;
        if (token.equals(cfg.booleanTrue))
            return true;
        if (token.equals(cfg.booleanFalse))
            return false;
        throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
            String.format("(%s, expected %s or %s for %s)", token, cfg.booleanTrue, cfg.booleanFalse, fieldname), parseIndex, currentClass);
    }
    

    @Override
    public String readAscii(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws MessageParserException {
        return readString(fieldname, allowNull, length, doTrim, doTruncate, false, false);
    }
    // readString does the job for Unicode as well as ASCII
    @Override
    public String readString(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws MessageParserException {
        String token = getField(fieldname, allowNull, length);
        if (token == null)
            return null;
        if (doTrim) {
            token = token.trim();
        }
        for (int i = 0; i < token.length(); ++i) {
            char c = token.charAt(i);
            if (allowUnicode) {
                // checks for Unicode characters
                if (c < ' ') {
                    if (allowCtrls && (c == '\t')) {
                        ; // special case: unescaped TAB character allowed
                    } else {
                        throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_CTRL, fieldname, parseIndex, currentClass);
                    }
                }
            } else {
                if (!CharTestsASCII.isAsciiPrintable(c)) {
                    throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_ASCII,
                            String.format("(found 0x%02x for %s)", (int)c, fieldname), parseIndex, currentClass);
                }
            }
        }
        if (length > 0) {
            // have limits on max size
            if (token.length() > length) {
                if (doTruncate) {
                    token = token.substring(0, length);
                } else {
                    throw new MessageParserException(MessageParserException.STRING_TOO_LONG,
                            String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, token),
                            parseIndex, currentClass);
                }
            }
        }
        return token;
    }

    @Override
    public BigDecimal readBigDecimal(String fieldname, boolean allowNull, int length, int decimals, boolean isSigned, boolean rounding, boolean autoScale) throws MessageParserException {
        int extra = fixedLength ? (decimals > 0 && !cfg.removePoint4BD ? 1 : 0) + (isSigned ? 1 : 0) : 0;
        String token = getField(fieldname, allowNull, length + extra);
        if (token == null)
            return null;
        BigDecimal r = new BigDecimal(token.trim());
        if (!isSigned && r.signum() < 0) {
            throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, fieldname, parseIndex, currentClass);
        }
        try {
            if (r.scale() > decimals)
                r = r.setScale(decimals, rounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
            if (autoScale && r.scale() < decimals)  // round for smaller as well!
                r = r.setScale(decimals, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException a) {
            throw new MessageParserException(MessageParserException.TOO_MANY_DECIMALS, fieldname, parseIndex, currentClass);
        }
        return r;
    }

    @Override
    public Character readCharacter(String fieldname, boolean allowNull) throws MessageParserException {
        String tmp = readString(fieldname, allowNull, 1, false, false, true, true);
        if (tmp == null) {
            return null;
        }
        if (tmp.length() == 0) {
            throw new MessageParserException(MessageParserException.EMPTY_CHAR, fieldname, parseIndex, currentClass);
        }
        return tmp.charAt(0);
    }

    @Override
    public ByteArray readByteArray(String fieldname, boolean allowNull, int length) throws MessageParserException {
        byte [] tmp = readRaw(fieldname, allowNull, length);
        if (tmp == null) {
            return null;
        }
        return new ByteArray(tmp); // TODO: this call does an unnecessary copy
    }

    @Override
    public byte[] readRaw(String fieldname, boolean allowNull, int length) throws MessageParserException {
        String token = getField(fieldname, allowNull, length);
        if (token == null)
            return null;
        try {
            byte [] btmp = token.getBytes();
            return Base64.decode(btmp, 0, btmp.length);
        } catch (IllegalArgumentException e) {
            throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, fieldname, parseIndex, currentClass);
        }
        // return DatatypeConverter.parseHexBinary(tmp);
    }

    @Override
    public Calendar readCalendar(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
        String token = getField(fieldname, allowNull, calendarFormatLength);  // will not work with fixed format!
        if (token == null)
            return null;
        GregorianCalendar result = new GregorianCalendar(cfg.locale);
        try {
            result.setTime(calendarFormat.parse(token));
        } catch (ParseException e) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname,
                    parseIndex, currentClass);
        }
        return result;
    }
    @Override
    public LocalDateTime readDayTime(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
        String token = getField(fieldname, allowNull, fractionalDigits > 0 ? timestamp3FormatLength : timestampFormatLength);
        if (token == null)
            return null;
        try {
            if (fractionalDigits > 0)
                return timestamp3Format.parseLocalDateTime(token);
            else
                return timestampFormat.parseLocalDateTime(token);
        } catch (Exception e) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname,
                    parseIndex, currentClass);
        }
    }
    @Override
    public LocalDate readDay(String fieldname, boolean allowNull) throws MessageParserException {
        String token = getField(fieldname, allowNull, dayFormatLength);
        if (token == null)
            return null;
        try {
            return dayFormat.parseLocalDate(token);
        } catch (Exception e) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname,
                    parseIndex, currentClass);
        }
    }
    
    @Override
    public int parseMapStart(String fieldname, boolean allowNull, int indexID) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, fieldname, parseIndex, currentClass);
    }

    @Override
    public int parseArrayStart(String fieldname, boolean allowNull, int max, int sizeOfChild) throws MessageParserException {
        Integer n = readInteger(fieldname, true, false);
        if (n == null) {
            if (!allowNull)
                throw new MessageParserException(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, fieldname, parseIndex, currentClass);
            return -1;
        }
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
    }

    @Override
    public BonaPortable readRecord() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readRecord()", parseIndex, currentClass);
    }


    @Override
    public Byte readByte(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 3+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Byte.valueOf(token.trim());
    }

    @Override
    public Short readShort(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 5+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Short.valueOf(token.trim());
    }

    @Override
    public Long readLong(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 18+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Long.valueOf(token.trim());
    }

    @Override
    public Integer readInteger(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 9+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Integer.valueOf(token.trim());
    }

    @Override
    public Float readFloat(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 10+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Float.valueOf(token.trim());
    }

    @Override
    public Double readDouble(String fieldname, boolean allowNull, boolean isSigned) throws MessageParserException {
        String token = getField(fieldname, allowNull, 18+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Double.valueOf(token.trim());
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
    }

    @Override
    public Integer readNumber(String fieldname, boolean allowNull, int length, boolean isSigned)
            throws MessageParserException {
        String token = getField(fieldname, allowNull, length+(isSigned ? 1 : 0));
        if (token == null)
            return null;
        return Integer.valueOf(token.trim());
    }

    @Override
    public BonaPortable readObject(String fieldname, Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws MessageParserException {
        if (allowSubtypes)
            throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readObject(subtypes = true)", parseIndex, currentClass);
        BonaPortable newObject;
        try {
            newObject = type.newInstance();
        } catch (InstantiationException e) {
            throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, "Instantiation exc on " + type.getCanonicalName(), parseIndex, currentClass);
        } catch (IllegalAccessException e) {
            throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, "Access exc on " + type.getCanonicalName(), parseIndex, currentClass);
        }
        
        String previousClass = currentClass;
        currentClass = newObject.get$PQON();
        newObject.deserialize(this);
        currentClass = previousClass;
        return newObject;
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readTransmission()", parseIndex, currentClass);
    }

    @Override
    public UUID readUUID(String fieldname, boolean allowNull) throws MessageParserException {
        String tmp = readAscii(fieldname, allowNull, 36, true, false);
        if (tmp == null) {
            return null;
        }
        try {
            return UUID.fromString(tmp);
        } catch (IllegalArgumentException e) {
            throw new MessageParserException(MessageParserException.BAD_UUID_FORMAT,
                    tmp, parseIndex, currentClass);
        }
    }

    @Override
    public MessageParserException enumExceptionConverter(EnumException e) {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.toString(), parseIndex, currentClass);
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }
}

