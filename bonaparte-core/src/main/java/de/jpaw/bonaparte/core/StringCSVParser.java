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
import java.util.List;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.Base64;
import de.jpaw.util.BigDecimalTools;
import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
/**
 * The StringCSVParser class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the deserialization of fixed width an quote-less CSV formats.
 *          Right now, only limited subsets are implemented. Especially date / time parsing is very limited.
 */


// TODO: should we convert "work" from String to CharSequence to make it more general?
public final class StringCSVParser extends StringBuilderConstants implements MessageParser<MessageParserException> {
    protected final CSVConfiguration cfg;
    private final boolean fixedLength;
    private final int lengthOfBoolean;
    private String work;                    // for parser. No longer final, as reusing the parser object makes sense due to the high number of datetime formatters constructed
    private int parseIndex;                 // for parser
    private int messageLength;              // for parser
    private String currentClass;
    protected final DateTimeFormatter dayFormat;            // day without time (Joda)
    protected final DateTimeFormatter timeFormat;           // time on second precision (Joda)
    protected final DateTimeFormatter time3Format;          // time on millisecond precision (Joda)
    protected final DateTimeFormatter timestampFormat;      // day and time on second precision (Joda)
    protected final DateTimeFormatter timestamp3Format;     // day and time on millisecond precision (Joda)
    protected final int dayFormatLength;            // day without time (Joda)
    protected final int timeFormatLength;           // time on second precision (Joda)
    protected final int time3FormatLength;          // time on millisecond precision (Joda)
    protected final int timestampFormatLength;      // day and time on second precision (Joda)
    protected final int timestamp3FormatLength;     // day and time on millisecond precision (Joda)

    public final void setSource(String src, int offset, int length) {
        work = src;
        parseIndex = offset;
        messageLength = length;
    }
    public final void setSource(String src) {
        work = src;
        parseIndex = 0;
        messageLength = src.length();
        // auto-truncate CR/LF, if it exists
        if (messageLength > 0 && work.charAt(messageLength-1) == '\n') {
            --messageLength;
        }
        if (messageLength > 0 && work.charAt(messageLength-1) == '\r') {
            --messageLength;
        }
    }
    
    public StringCSVParser(CSVConfiguration cfg, String work) {
        // strip CR/LF from input, if existing
        setSource(work);
        this.cfg = cfg;
        this.lengthOfBoolean = cfg.booleanFalse.length() > cfg.booleanTrue.length() ? cfg.booleanFalse.length() : cfg.booleanTrue.length();
        this.dayFormat = cfg.determineDayFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timeFormat = cfg.determineTimeFormatter().withLocale(cfg.locale).withZoneUTC();
        this.time3Format = cfg.determineTime3Formatter().withLocale(cfg.locale).withZoneUTC();
        this.timestampFormat = cfg.determineTimestampFormatter().withLocale(cfg.locale).withZoneUTC();
        this.timestamp3Format = cfg.determineTimestamp3Formatter().withLocale(cfg.locale).withZoneUTC();
        this.dayFormatLength = cfg.customDayFormat == null ? 8 : cfg.customDayFormat.length();
        this.timeFormatLength = cfg.customTimeFormat == null ? 6 : cfg.customTimeFormat.length();
        this.time3FormatLength = cfg.customTimeWithMsFormat == null ? 9 : cfg.customTimeWithMsFormat.length();
        this.timestampFormatLength = cfg.customTimestampFormat == null ? 14 : cfg.customTimestampFormat.length();
        this.timestamp3FormatLength = cfg.customTimestampWithMsFormat == null ? 17 : cfg.customTimestampWithMsFormat.length();
        fixedLength = cfg.separator.length() == 0;
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
            	result = "";  // not required, but avoid warnings below...
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
                        // special case: unescaped TAB character allowed
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
        return BigDecimalTools.checkAndScale(r, length, decimals, isSigned, rounding, autoScale, fieldname, parseIndex, currentClass);
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
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname, parseIndex, currentClass);
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
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname, parseIndex, currentClass);
        }
    }
    @Override
    public LocalTime readTime(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
        String token = getField(fieldname, allowNull, fractionalDigits > 0 ? timestamp3FormatLength : timestampFormatLength);
        if (token == null)
            return null;
        try {
            if (fractionalDigits > 0)
                return time3Format.parseLocalTime(token);
            else
                return timeFormat.parseLocalTime(token);
        } catch (Exception e) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fieldname, parseIndex, currentClass);
        }
    }

    @Override
    public Instant readInstant(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
        String tmp = getField(fieldname, allowNull, 19);
        if (tmp == null)
            return null;
        int millis = 0;
        long seconds = 0;
        int dpoint;
        if ((dpoint = tmp.indexOf('.')) < 0) {
            seconds = Long.parseLong(tmp);  // only seconds
        } else {
            // seconds and millis seconds
            seconds = Long.parseLong(tmp.substring(0, dpoint));
            millis = Integer.parseInt(tmp.substring(dpoint + 1));
            switch (tmp.length() - dpoint - 1) { // i.e. number of fractional digits
            case 2:
                millis *= 10;
                break;
            case 1:
                millis *= 100;
                break;
            case 3:
                break; // maximum resolution (milliseconds)
            default: // something weird
                throw new MessageParserException(
                        MessageParserException.BAD_TIMESTAMP_FRACTIONALS,
                        String.format("(found %d for %s)", tmp.length() - dpoint - 1, fieldname),
                        parseIndex, currentClass);
            }
        }
        return new Instant(1000L * seconds + millis);
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
    public MessageParserException enumExceptionConverter(IllegalArgumentException e) {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage(), parseIndex, currentClass);
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }

	@Override
	public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
		XEnumDefinition spec = di.getBaseXEnum();
		String scannedToken = readString(di.getName(), !di.getIsRequired() || spec.getHasNullToken(), spec.getMaxTokenLength(), true, false, false, true);
		if (scannedToken == null)
			return factory.getNullToken();
		T value = factory.getByToken(scannedToken);
		if (value == null) {
			throw new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, scannedToken, parseIndex, currentClass + "." + di.getName());
		}
		return value;
	}
}

