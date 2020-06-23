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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

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
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
import de.jpaw.util.IntegralLimits;
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
public final class StringCSVParser extends AbstractPartialJsonStringParser implements MessageParser<MessageParserException>, StringBuilderConstants {
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
    protected CSVObjectTypeDetector objectTypeDetector = null;
    protected NumberFormat localBigDecimalFormat = null;      // if set (via setNationalBigDecimal)
    protected NumberFormat localFloatFormat      = null;      // if set (via setNationalFloat)

    public void setNationalBigDecimal() {
        localBigDecimalFormat = NumberFormat.getInstance(cfg.locale);
        if (localBigDecimalFormat instanceof DecimalFormat) {
            ((DecimalFormat)localBigDecimalFormat).setParseBigDecimal(true);
        }
    }
    public void setNationalFloat() {
        localFloatFormat = NumberFormat.getInstance(cfg.locale);
    }


    /** Define the method to guess the type of the record by inspecting its contents. */
    public static interface CSVObjectTypeDetector {
        Class<? extends BonaPortable> typeByContents(String msg) throws MessageParserException;
    }

    public static abstract class AbstractCSVObjectTypeDetector implements CSVObjectTypeDetector {
        protected final Map<String, Class<? extends BonaPortable>> recordMap;

        public AbstractCSVObjectTypeDetector(Map<String, Class<? extends BonaPortable>> recordMap) {
            this.recordMap = recordMap;
        }
    }

    /** Determines the object type based on the contents of the first field, using a delimiter. */
    public static class DelimiterBasedObjectTypeDetector extends AbstractCSVObjectTypeDetector {
        protected final String delimiter;

        public DelimiterBasedObjectTypeDetector(Map<String, Class<? extends BonaPortable>> recordMap, String delimiter) {
            super(recordMap);
            this.delimiter = delimiter;
        }

        @Override
        public Class<? extends BonaPortable> typeByContents(String msg) throws MessageParserException {
            int pos = msg.indexOf(delimiter);
            String key = pos < 0 ? msg : msg.substring(0, pos); // if pos < 0: record types such as "EOF" etc... these are valid
            return recordMap.get(key.trim());
        }
    }
    /** Determines the object type based on the contents of the first n characters. */
    public static class FixedWidthObjectTypeDetector extends AbstractCSVObjectTypeDetector {
        protected final int widthOfFirstField;

        public FixedWidthObjectTypeDetector(Map<String, Class<? extends BonaPortable>> recordMap, int widthOfFirstField) {
            super(recordMap);
            this.widthOfFirstField = widthOfFirstField;
        }

        @Override
        public Class<? extends BonaPortable> typeByContents(String msg) throws MessageParserException {
            String key = msg.length() < widthOfFirstField ? msg : msg.substring(0, widthOfFirstField);
            return recordMap.get(key.trim());
        }
    }

    /** Defines the portion of src from offset (inclusive) to length (exclusive) as parsing source, i.e. length - offset characters. */
    public final void setSource(String src, int offset, int length) {
        // auto-truncate CR/LF, if it exists
        if (length > 0 && src.charAt(length-1) == '\n') {
            --length;
        }
        if (length > 0 && src.charAt(length-1) == '\r') {
            --length;
        }
        if (length < src.length()) {
            // some truncation done: remove it from the buffer!
            work = src.substring(offset, length);
            parseIndex = 0;
            messageLength = work.length();
        } else {
            // a copy is not needed
            work = src;
            parseIndex = offset;
            messageLength = length;
        }
    }

    /** Defines src as parsing source. */
    public final void setSource(String src) {
        setSource(src, 0, src.length());
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

    public StringCSVParser(CSVConfiguration cfg, String work, CSVObjectTypeDetector objectTypeDetector) {
        this(cfg, work);
        this.objectTypeDetector = objectTypeDetector;
    }

    // setting this allows to use readRecord in subsequent calls
    public void setMapping(CSVObjectTypeDetector objectTypeDetector) {
        this.objectTypeDetector = objectTypeDetector;
    }

    @Override
    protected MessageParserException newMPE(int errorCode, FieldDefinition di, String msg) {
        return new MessageParserException(errorCode, di.getName(), parseIndex, currentClass, msg);
    }


    /**************************************************************************************************
     * Deserialization goes here
     **************************************************************************************************/

    protected String processTrailingSigns(String token) {
        token = token.trim();
        // check for trailing sign
        int l = token.length();
        if (l > 0 && token.charAt(l-1) == '-')
            token = "-" + token.substring(0, l-1);  // move sign to the start of the string
        return token;
    }

    private String getField(String fieldname, boolean isRequired, int length) throws MessageParserException {
        // System.out.println("parsing " + fieldname + " for length " + length);
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
            // first, check for a quote delimited string
            if (cfg.quote != null && parseIndex < messageLength && work.charAt(parseIndex) == cfg.quote.charValue()) {
                // yes, is a quoted string: read characters up to the next quote, and eat that
                int index = work.indexOf(cfg.quote.charValue(), parseIndex+1);
                if (index < 0) {
                    throw new MessageParserException(MessageParserException.MISSING_CLOSING_QUOTE, fieldname, parseIndex, currentClass);
                } else {
                    result = work.substring(parseIndex+1, index);
                    parseIndex = index + 1; // skip field and quote
                    // now expect the delimiter, or an end
                    if (parseIndex < messageLength) {
                        // at least one character more
                        if (work.indexOf(cfg.separator, parseIndex) == parseIndex) {
                            ++parseIndex;  // FIXME: this assumes length of separator is 1
                        } else {
                            throw new MessageParserException(MessageParserException.MISSING_FIELD_TERMINATOR, fieldname, parseIndex, currentClass);
                        }
                    }
                }
            } else {
                // no, read up to the next delimited
                int index = work.indexOf(cfg.separator, parseIndex);
                if (index < 0) {
                    result = work.substring(parseIndex);
                    parseIndex = messageLength;
                } else {
                    result = work.substring(parseIndex, index);
                    parseIndex = index + 1; // skip field and separator  // FIXME: this assumes length of separator is 1
                }
                if (result.length() == 0)
                    result = null;
            }
        }
        if (result == null && isRequired)
            throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
        return result;
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), lengthOfBoolean);
        if (token == null)
            return null;
        if (token.equals(cfg.booleanTrue))
            return Boolean.TRUE;
        if (token.equals(cfg.booleanFalse))
            return Boolean.FALSE;
        throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
            String.format("(%s, expected %s or %s for %s)", token, cfg.booleanTrue, cfg.booleanFalse, di.getName()), parseIndex, currentClass);
    }


    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), false);
    }
    // readString does the job for Unicode as well as ASCII
    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), true);
    }

    protected String readString(String fieldname, boolean isRequired, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws MessageParserException {
        String token = getField(fieldname, isRequired, length);
        if (token == null)
            return null;
        if (doTrim && !fixedLength) {
            token = token.trim();       // CSV formats trim by default
            if (token.length() == 0 && cfg.quote == null) {
                return null;            // convert empty string to null, if unquoted
            }
        }
        for (int i = 0; i < token.length(); ++i) {
            char c = token.charAt(i);
            if (allowUnicode) {
                // checks for Unicode characters
                if (c < ' ') {
                    if (c != '\t' && !allowCtrls) {
                        // special control character, not TAB, and not allowed
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
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        int extra = fixedLength ? (di.getDecimalDigits() > 0 && !cfg.removePoint4BD ? 1 : 0) + (di.getIsSigned() ? 1 : 0) : 0;
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits() + extra);
        if (token == null)
            return null;
        BigDecimal r;
        if (localBigDecimalFormat != null) {
            try {
                r = (BigDecimal) localBigDecimalFormat.parse(processTrailingSigns(token));
            } catch (ParseException e) {
                throw new MessageParserException(MessageParserException.NUMBER_PARSING_ERROR, di.getName(), parseIndex, currentClass);
            }
        } else {
            try {
                r = new BigDecimal(processTrailingSigns(token));
            } catch (NumberFormatException e) {
                throw new MessageParserException(MessageParserException.NUMBER_PARSING_ERROR, di.getName(), parseIndex, currentClass);
            }
        }
        return BigDecimalTools.checkAndScale(r, di, parseIndex, currentClass);
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        String tmp = readString(di.getName(), di.getIsRequired(), 1, false, false, true, true);
        if (tmp == null) {
            return null;
        }
        if (tmp.length() == 0) {
            throw new MessageParserException(MessageParserException.EMPTY_CHAR, di.getName(), parseIndex, currentClass);
        }
        return tmp.charAt(0);
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        byte [] tmp = readRaw(di);
        if (tmp == null) {
            return null;
        }
        return new ByteArray(tmp); // TODO: this call does an unnecessary copy
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getLength());
        if (token == null)
            return null;
        try {
            byte [] btmp = token.getBytes();
            return Base64.decode(btmp, 0, btmp.length);
        } catch (IllegalArgumentException e) {
            throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, di.getName(), parseIndex, currentClass);
        }
        // return DatatypeConverter.parseHexBinary(tmp);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getFractionalSeconds() > 0 ? timestamp3FormatLength : timestampFormatLength);
        if (token == null)
            return null;
        try {
            if (di.getFractionalSeconds() > 0)
                return timestamp3Format.parseLocalDateTime(token);
            else
                return timestampFormat.parseLocalDateTime(token);
        } catch (Exception e) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, di.getName(), parseIndex, currentClass);
        }
    }
    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), dayFormatLength);
        if (token == null)
            return null;
        try {
            return dayFormat.parseLocalDate(token);
        } catch (Exception e) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, di.getName(), parseIndex, currentClass);
        }
    }
    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getFractionalSeconds() > 0 ? timestamp3FormatLength : timestampFormatLength);
        if (token == null)
            return null;
        try {
            if (di.getFractionalSeconds() > 0)
                return time3Format.parseLocalTime(token);
            else
                return timeFormat.parseLocalTime(token);
        } catch (Exception e) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, di.getName(), parseIndex, currentClass);
        }
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        String tmp = getField(di.getName(), di.getIsRequired(), 19);
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
                        String.format("(found %d for %s)", tmp.length() - dpoint - 1, di.getName()),
                        parseIndex, currentClass);
            }
        }
        return new Instant(1000L * seconds + millis);
    }


    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, di.getName(), parseIndex, currentClass);
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfChild) throws MessageParserException {
        String token = getField(di.getName(), false, 9);
        if (token == null)
            return -1;
        token = token.trim();
        if (token == null || token.length() == 0) {
            if (di.getIsAggregateRequired())
                throw new MessageParserException(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, di.getName(), parseIndex, currentClass);
            return -1;
        }
        int n = Integer.parseInt(token);
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, di.getName()), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
    }

    @Override
    public BonaPortable readRecord() throws MessageParserException {
        if (objectTypeDetector == null) {
            // parsing an arbitrary object is not possible here because we have no type information
            throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readRecord()", parseIndex, currentClass);
        }
        Class<? extends BonaPortable> mappedClass = objectTypeDetector.typeByContents(work);
        if (mappedClass == null)
            throw new MessageParserException(MessageParserException.UNKNOW_RECORD_TYPE, work, parseIndex, currentClass);
        return readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, mappedClass);
    }

    private String readBufferForInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        String tmp = getField(di.getName(), di.getIsRequired(), di.getTotalDigits() + (di.getIsSigned() ? 1 : 0) + (!cfg.removePoint4BD && di.getDecimalDigits() > 0 ? 1 : 0));
        if (tmp != null) {
            tmp = tmp.trim();
            if (tmp.length() == 0)
                return null;
        }
        return tmp;
    }
    private long postProcessForImplicitDecimals(BasicNumericElementaryDataItem di, String token) throws MessageParserException {
        token = processTrailingSigns(token);
        int decimals = di.getDecimalDigits();

        // run it through a BigDecimal for now...
        BigDecimal tmp = new BigDecimal(token);
        if (tmp.signum() == 0)
            return 0L; // always valid
        // set the scale
        BigDecimal vv = tmp.setScale(decimals, di.getRounding() ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        long ltmp = vv.unscaledValue().longValue();
        // verify that we do not exceed bounds...
        if (ltmp < 0L && !di.getIsSigned())
            throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, di.getName(), parseIndex, currentClass);
        // make sure that the parsed value does not exceed the configured number of digits
        if (ltmp < IntegralLimits.LONG_MIN_VALUES[di.getTotalDigits()] ||
            ltmp > IntegralLimits.LONG_MAX_VALUES[di.getTotalDigits()])
            throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG, di.getName(), parseIndex, currentClass);
        return ltmp;
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (token == null)
            return null;
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Byte.valueOf(processTrailingSigns(token));
        return Byte.valueOf((byte) postProcessForImplicitDecimals(di, token));
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (token == null)
            return null;
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Short.valueOf(processTrailingSigns(token));
        return Short.valueOf((short) postProcessForImplicitDecimals(di, token));
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (token == null)
            return null;
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Integer.valueOf(processTrailingSigns(token));
        return Integer.valueOf((int) postProcessForImplicitDecimals(di, token));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (token == null)
            return null;
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Long.valueOf(processTrailingSigns(token));
        return Long.valueOf(postProcessForImplicitDecimals(di, token));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits()+(di.getIsSigned() ? 1 : 0));
        if (token == null)
            return null;
        return parseFloat(token.trim(), di);
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits()+(di.getIsSigned() ? 1 : 0));
        if (token == null)
            return null;
        return Double.valueOf(token.trim());
    }

    protected Float parseFloat(String s, BasicNumericElementaryDataItem di) throws MessageParserException {
        if (localFloatFormat != null) {
            try {
                return (Float) localFloatFormat.parse(processTrailingSigns(s));
            } catch (ParseException e) {
                throw new MessageParserException(MessageParserException.NUMBER_PARSING_ERROR, di.getName(), parseIndex, currentClass);
            }
        } else {
            return Float.valueOf(s);
        }
    }

    protected Double parseDouble(String s, BasicNumericElementaryDataItem di) throws MessageParserException {
        if (localFloatFormat != null) {
            try {
                return (Double) localFloatFormat.parse(processTrailingSigns(s));
            } catch (ParseException e) {
                throw new MessageParserException(MessageParserException.NUMBER_PARSING_ERROR, di.getName(), parseIndex, currentClass);
            }
        } else {
            return Double.valueOf(s);
        }
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits()+(di.getIsSigned() ? 1 : 0));
        if (token == null)
            return null;
        return new BigInteger(processTrailingSigns(token));
    }

    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws MessageParserException {
        if (di.getAllowSubclasses())
            throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readObject(subtypes = true)", parseIndex, currentClass);
        R newObject;
        try {
            newObject = type.newInstance();
        } catch (InstantiationException e) {
            throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, "Instantiation exc on " + type.getCanonicalName(), parseIndex, currentClass);
        } catch (IllegalAccessException e) {
            throw new MessageParserException(MessageParserException.CLASS_NOT_FOUND, "Access exc on " + type.getCanonicalName(), parseIndex, currentClass);
        }

        String previousClass = currentClass;
        currentClass = newObject.ret$PQON();
        newObject.deserialize(this);
        currentClass = previousClass;
        return newObject;
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, "readTransmission()", parseIndex, currentClass);
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        String tmp = readString(di.getName(), di.getIsRequired(), 36, false, false, false, false);
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
        String scannedToken = readString(di.getName(), di.getIsRequired() && !spec.getHasNullToken(), spec.getMaxTokenLength(), true, false, false, true);
        if (scannedToken == null)
            return factory.getNullToken();
        T value = factory.getByToken(scannedToken);
        if (value == null) {
            throw new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, scannedToken, parseIndex, currentClass + "." + di.getName());
        }
        return value;
    }

    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), true, lengthOfBoolean);
        if (token.equals(cfg.booleanTrue))
            return true;
        if (token.equals(cfg.booleanFalse))
            return false;
        throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
            String.format("(%s, expected %s or %s for %s)", token, cfg.booleanTrue, cfg.booleanFalse, di.getName()), parseIndex, currentClass);
    }

    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws MessageParserException {
        String tmp = readString(di.getName(), true, 1, false, false, true, true);
        if (tmp.length() == 0) {
            throw new MessageParserException(MessageParserException.EMPTY_CHAR, di.getName(), parseIndex, currentClass);
        }
        return tmp.charAt(0);
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits()+(di.getIsSigned() ? 1 : 0));
        return parseDouble(token.trim(), di);
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = getField(di.getName(), di.getIsRequired(), di.getTotalDigits()+(di.getIsSigned() ? 1 : 0));
        return parseFloat(token.trim(), di);
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Long.parseLong(processTrailingSigns(token));
        return postProcessForImplicitDecimals(di, token);
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Integer.parseInt(processTrailingSigns(token));
        return (int) postProcessForImplicitDecimals(di, token);
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Short.parseShort(processTrailingSigns(token));
        return (short) postProcessForImplicitDecimals(di, token);
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        String token = readBufferForInteger(di);
        if (cfg.removePoint4BD || di.getDecimalDigits() == 0)
            return Byte.parseByte(processTrailingSigns(token));
        return (byte) postProcessForImplicitDecimals(di, token);
    }

    @Override
    protected String getString(FieldDefinition di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), Integer.MAX_VALUE, true, false, true, true);
    }
}
