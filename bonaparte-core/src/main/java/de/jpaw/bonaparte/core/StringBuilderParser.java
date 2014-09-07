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
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.Base64;
import de.jpaw.util.BigDecimalTools;
import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import javax.xml.bind.DatatypeConverter;
/**
 * The StringBuilderParser class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the deserialization for the bonaparte format using StringBuilder.
 */

public final class StringBuilderParser extends StringBuilderConstants implements MessageParser<MessageParserException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringBuilderParser.class);
    private CharSequence work;          // for parser
    private int parseIndex;             // for parser
    private int messageLength;          // for parser
    private String currentClass;
    private final boolean useCache = true;
    private List<BonaPortable> objects;

    public final void setSource(CharSequence src, int offset, int length) {
        work = src;
        parseIndex = offset;
        messageLength = length;
    }
    public final void setSource(CharSequence src) {
        work = src;
        parseIndex = 0;
        messageLength = src.length();
    }
    public StringBuilderParser(CharSequence work, int offset, int length) {
        setSource(work, offset, length < 0 ? work.length() : length); // -1 means full array size
        currentClass = "N/A";
        if (useCache)
            objects = new ArrayList<BonaPortable>(60);
    }

    /**************************************************************************************************
     * Deserialization goes here
     **************************************************************************************************/

    private char needToken() throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END, null, parseIndex, currentClass);
        }
        return work.charAt(parseIndex++);
    }

    private void needToken(char c) throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END,
                    String.format("(expected 0x%02x)", (int)c), parseIndex, currentClass);
        }
        char d = work.charAt(parseIndex++);
        if (c != d) {
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected 0x%02x, got 0x%02x)", (int)c, (int)d), parseIndex, currentClass);
        }
    }

    /* If byte c occurs, eat it */
    private void skipChar(char c) {
        if ((parseIndex < messageLength) && (work.charAt(parseIndex) == c)) {
            ++parseIndex;
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(FieldDefinition di) throws MessageParserException {
        return checkForNull(di.getName(), di.getIsRequired());
    }
    protected Integer readInteger(String fieldname) throws MessageParserException {
        checkForNull(fieldname, true);
        return Integer.valueOf(nextIndexParseAscii(fieldname, false, false, false));
    }
    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean isRequired) throws MessageParserException {
        char c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == ARRAY_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
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

    private void skipLeadingSpaces() {
        while (parseIndex < messageLength) {
            char c = work.charAt(parseIndex);
            if ((c != ' ') && (c != '\t')) {
                break;
            }
            // skip leading blanks
            ++parseIndex;
        }
    }

    private void skipNulls() {
        while (parseIndex < messageLength) {
            char c = work.charAt(parseIndex);
            if (c != NULL_FIELD) {
                break;
            }
            // skip trailing NULL objects
            ++parseIndex;
        }
    }

    private String nextIndexParseAscii(String fieldname, boolean allowSign, boolean allowDecimalPoint, boolean allowExponent) throws MessageParserException {
        final int BUFFER_SIZE = 40;
        boolean allowSignNextIteration = false;
        boolean gotAnyDigit = false;
        StringBuffer tmp = new StringBuffer(BUFFER_SIZE);
        // skipBlanks: does not hurt!
        skipLeadingSpaces();
        if ((parseIndex < messageLength) && (work.charAt(parseIndex) == '+')) {
            // allow positive sign in any case (but not followed by a minus)
            ++parseIndex;
            allowSign = false;
        }
        while (parseIndex < messageLength) {
            char c = work.charAt(parseIndex);
            if (c == FIELD_TERMINATOR) {
                if (!gotAnyDigit) {
                    throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
                }
                ++parseIndex;  // eat it!
                return tmp.toString();
            }

            if (c == '-') {
                if (!allowSign) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, fieldname, parseIndex, currentClass);
                }
            } else if (c == '.') {
                if (!allowDecimalPoint) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_DECIMAL_POINT, fieldname, parseIndex, currentClass);
                }
                allowDecimalPoint = false;  // no 2 in a row allowed
            } else if ((c == 'e') || (c == 'E')) {
                if (!allowExponent) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_EXPONENT, fieldname, parseIndex, currentClass);
                }
                if (!gotAnyDigit) {
                    throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
                }
                allowSignNextIteration = true;
                allowExponent = false;
                allowDecimalPoint = false;
            } else if (CharTestsASCII.isAsciiDigit(c)) {
                gotAnyDigit = true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
            }
            if (tmp.length() >= BUFFER_SIZE) {
                throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG, fieldname, parseIndex, currentClass);
            }
            tmp.append(c);
            ++parseIndex;
            allowSign = allowSignNextIteration;
            allowSignNextIteration = false;
        }
        // end of message without appropriate terminator character
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
    }

    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        BigDecimal r = new BigDecimal(nextIndexParseAscii(di.getName(), di.getIsSigned(), true, false));
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
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), false);
    }
    // readString does the job for Unicode as well as ASCII
    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), true);
    }
    
    protected String readString(String fieldname, boolean isRequired, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws MessageParserException {
        if (checkForNull(fieldname, isRequired)) {
            return null;
        }
        // OK, read it
        StringBuffer tmp = new StringBuffer(length == 0 ? 32 : length);
        char c;
        if (doTrim) {
            // skip leading spaces
            skipLeadingSpaces();
        }
        while ((c = needToken()) != FIELD_TERMINATOR) {
            if (allowUnicode) {
                // checks for Unicode characters
                if (c < ' ') {
                    if (allowCtrls && (c == '\t')) {
                        // special case: unescaped TAB character allowed
                    } else if (allowCtrls && (c == ESCAPE_CHAR)) {
                        c = needToken();
                        if ((c < 0x40) || (c >= 0x60)) {
                            throw new MessageParserException(MessageParserException.ILLEGAL_ESCAPE_SEQUENCE,
                                    String.format("(found 0x%02x for %s)", (int)c, fieldname), parseIndex, currentClass);
                        }
                        c -= 0x40;
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
            tmp.append(c);
        }
        if (doTrim) {
            int l = tmp.length();
            // trim trailing blanks
            while (l > 0) {
                char d = tmp.charAt(l-1);
                if ((d != ' ') && (d != '\t'))
                {
                    break;  // l is correct length
                }
                --l;
            }
            if (l < tmp.length()) {
                tmp.setLength(l);
            }
        }
        if (length > 0) {
            // have limits on max size
            if (tmp.length() > length) {
                if (doTruncate) {
                    tmp.setLength(length);
                } else {
                    throw new MessageParserException(MessageParserException.STRING_TOO_LONG,
                            String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, tmp.toString()),
                            parseIndex, currentClass);
                }
            }
        }
        return tmp.toString();
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        boolean result;
        if (checkForNull(di)) {
            return null;
        }
        char c = needToken();
        if (c == '0') {
            result = false;
        } else if (c == '1') {
            result = true;
        } else {
            throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
                    String.format("(found 0x%02x for %s)", (int)c, di.getName()), parseIndex, currentClass);
        }
        needToken(FIELD_TERMINATOR);
        return result;
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
        if (checkForNull(di)) {
            return null;
        }
        int i = parseIndex;
        // find next occurence of field terminator
        while ((i < messageLength) && (work.charAt(i) != FIELD_TERMINATOR)) {
            ++i;
        }
        if (i == messageLength) {
            throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, di.getName(), parseIndex, currentClass);
        }
        String tmp = work.subSequence(parseIndex, i).toString();  // TODO: too many temporary objects created. This could be improved.
        parseIndex = i+1;
        try {
            byte [] btmp = tmp.getBytes();
            return Base64.decode(btmp, 0, btmp.length);
        } catch (IllegalArgumentException e) {
            throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, di.getName(), parseIndex, currentClass);
        }
        // return DatatypeConverter.parseHexBinary(tmp);
    }
    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        String tmp = nextIndexParseAscii(di.getName(), false, di.getFractionalSeconds() >= 0, false);  // parse an unsigned numeric string without exponent
        int date;
        int fractional = 0;
        int dpoint;
        if ((dpoint = tmp.indexOf('.')) < 0) {
            // day only despite allowed time
            date = Integer.parseInt(tmp);
        } else {
            // day and time
            date = Integer.parseInt(tmp.substring(0, dpoint));
            fractional = Integer.parseInt(tmp.substring(dpoint + 1));
            switch (tmp.length() - dpoint - 1) { // i.e. number of fractional digits
            case 6:
                fractional *= 1000;
                break; // precisely seconds resolution (timestamp(0))
            case 7:
                fractional *= 100;
                break;
            case 8:
                fractional *= 10;
                break;
            case 9:
                break; // maximum resolution (milliseconds)
            default: // something weird
                throw new MessageParserException(
                        MessageParserException.BAD_TIMESTAMP_FRACTIONALS,
                        String.format("(found %d for %s)", tmp.length() - dpoint - 1, di.getName()),
                        parseIndex, currentClass);
            }
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
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_DAY, String.format(
                            "(found %d for %s)", year*10000+month*100+day, di.getName()), parseIndex, currentClass);
        }
        if ((hour > 23) || (minute > 59) || (second > 59)) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_TIME,
                    String.format("(found %d for %s)", (hour * 10000) + (minute * 100)
                            + second, di.getName()), parseIndex, currentClass);
        }
        // now set the return value
        LocalDateTime result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = new LocalDateTime(year, month, day, hour, minute, second, fractional);
        } catch (Exception e) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_CALENDAR_VALUE, di.getName(),
                    parseIndex, currentClass);
        }
        return result;
    }
    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        String tmp = nextIndexParseAscii(di.getName(), false, false, false);  // parse an unsigned numeric string without exponent
        int date = Integer.parseInt(tmp);
        // set the date and time
        int day, month, year;
        year = date / 10000;
        month = (date %= 10000) / 100;
        day = date %= 100;
        // first checks
        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
                || (day > 31)) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_DAY, String.format(
                            "(found %d for %s)", year*10000+month*100+day, di.getName()), parseIndex, currentClass);
        }
        // now set the return value
        LocalDate result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = new LocalDate(year, month, day);
        } catch (Exception e) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_CALENDAR_VALUE, di.getName(),
                    parseIndex, currentClass);
        }
        return result;
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        String tmp = nextIndexParseAscii(di.getName(), false, di.getFractionalSeconds() > 0, false);  // parse an unsigned numeric string without exponent
        int millis = 0;
        int seconds = 0;
        int dpoint;
        if ((dpoint = tmp.indexOf('.')) < 0) {
            seconds = Integer.parseInt(tmp);  // only seconds
        } else {
            // seconds and millis seconds
            seconds = Integer.parseInt(tmp.substring(0, dpoint));
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
        // set the date and time
        int hour, minute, second;
        if (di.getHhmmss()) {
            hour = seconds / 10000;
            minute = (seconds % 10000) / 100;
            second = seconds % 100;
            seconds = 3600 * hour + 60 * minute + second;  // convert to seconds of day
        } else {
            hour = seconds / 3600;
            minute = (seconds % 3600) / 60;
            second = seconds % 60;
        }
        // first checks
        if ((hour > 23) || (minute > 59) || (second > 59)) {
            throw new MessageParserException(
                    MessageParserException.ILLEGAL_TIME,
                    String.format("(found %d for %s)", (hour * 10000) + (minute * 100) + second, di.getName()), parseIndex, currentClass);
        }
        return new LocalTime(1000 * seconds + millis, DateTimeZone.UTC);
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        String tmp = nextIndexParseAscii(di.getName(), false, di.getFractionalSeconds() > 0, false);  // parse an unsigned numeric string without exponent
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
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {  // check it separately in order to give a distinct error message
            if (di.getIsAggregateRequired())
                throw new MessageParserException(MessageParserException.NULL_MAP_NOT_ALLOWED_HERE, fieldname, parseIndex, currentClass);
            return -1;
        }
        needToken(MAP_BEGIN);
        int foundIndexType = readInteger(fieldname);
        if (foundIndexType != di.getMapIndexType()) {
            throw new MessageParserException(MessageParserException.WRONG_MAP_INDEX_TYPE,
                    String.format("(got %d, expected for %s)", foundIndexType, di.getMapIndexType(), fieldname), parseIndex, currentClass);
        }
        int n = readInteger(fieldname);
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfChild) throws MessageParserException {
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {
            if (di.getIsAggregateRequired())
                throw new MessageParserException(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, fieldname, parseIndex, currentClass);
            return -1;
        }
        needToken(ARRAY_BEGIN);
        int n = readInteger(fieldname);
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
        needToken(ARRAY_TERMINATOR);

    }

    protected void skipOptionalBom() throws MessageParserException {
        if (needToken() != BOM) {
            --parseIndex;  // uneat it
        } // else: skip it and expect RECORD_BEGIN
    }
    
    @Override
    public BonaPortable readRecord() throws MessageParserException {
        BonaPortable result;
        skipOptionalBom();
        needToken(RECORD_BEGIN);
        needToken(NULL_FIELD); // version no
        result = readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
        skipChar(RECORD_OPT_TERMINATOR);
        needToken(RECORD_TERMINATOR);
        return result;
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Byte.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Short.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Integer.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Long.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Float.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), true, true));
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return Double.valueOf(nextIndexParseAscii(di.getName(), di.getIsSigned(), true, true));
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
        eatObjectOrParentSeparator(PARENT_SEPARATOR);
    }       
        
    public void eatObjectTerminator() throws MessageParserException {
        eatObjectOrParentSeparator(OBJECT_TERMINATOR);
    }
    
    protected void eatObjectOrParentSeparator(char which) throws MessageParserException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        char z = needToken();
        if (z == which)
            return;   // all good
        
        // temporarily provide compatibility to 1.7.9 and back...
        if (z == PARENT_SEPARATOR) {
            // implies we have been looking for OBJECT_TERMINATOR...
            return;
        }
        
        // we have extra data and it is not null. Now the behavior depends on a parser setting
        ParseSkipNonNulls mySetting = getSkipNonNullsBehavior();
        switch (mySetting) {
        case ERROR:
            throw new MessageParserException(MessageParserException.EXTRA_FIELDS, String.format("(found byte 0x%02x)", z), parseIndex, currentClass);  
        case WARN:
            LOGGER.warn("{} at index {} parsing class {}", MessageParserException.codeToString(MessageParserException.EXTRA_FIELDS), parseIndex, currentClass);
            // fall through
        case IGNORE:
            // skip bytes until we are at end of record (bad!) (thrown by needToken()) or find the terminator
            skipUntilNext(which);
        }
    }
    
    protected void skipUntilNext(char which) throws MessageParserException {
        char c;
        while ((c = needToken()) != which) {
            if (c == OBJECT_BEGIN) {
                // skip nested object!
                skipUntilNext(OBJECT_TERMINATOR);
            }
        }
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        String tmp = nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false);
        if (tmp.length() > (di.getTotalDigits() + (((tmp.charAt(0) == '-') || (tmp.charAt(0) == '+')) ? 1 : 0))) {
            throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG,
                    String.format("(allowed %d, found %d for %s)", di.getTotalDigits(), tmp.length(), di.getName()), parseIndex, currentClass);
        }
        return new BigInteger(tmp);
    }

    @Override
    public BonaPortable readObject(ObjectReference di, Class<? extends BonaPortable> type) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        boolean allowSubtypes = di.getAllowSubclasses();
        String fieldname = di.getName();
        if (useCache && parseIndex < messageLength && work.charAt(parseIndex) == OBJECT_AGAIN) {
            // we reuse an object
            ++parseIndex;
            int objectIndex = readInteger(fieldname).intValue();
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
            return newObject;
        } else {
            needToken(OBJECT_BEGIN); // version not yet allowed
            String previousClass = currentClass;
            String classname = readString(fieldname, true, 0, true, false, false, false);
            // String revision = readAscii(true, 0, false, false);
            needToken(NULL_FIELD); // version not yet allowed
            BonaPortable newObject = BonaPortableFactory.createObject(classname);
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
            eatObjectTerminator();
            currentClass = previousClass;
            return newObject;
        }
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        char c = needToken();
        if (c == TRANSMISSION_BEGIN) {
            needToken(NULL_FIELD);  // version
            // TODO: parse extensions here
            while ((c = needToken()) != TRANSMISSION_TERMINATOR) {
                // System.out.println("transmission loop: char is " + c);
                --parseIndex; // push back object def
                results.add(readRecord());
            }
            // when here, last char was transmission terminator
            // optionally eat the last one as well?
        } else if (c == RECORD_BEGIN /* || c == EXTENSION_BEGIN */) {
            // allow single record as a special case
            // TODO: parse extensions here
            --parseIndex;
            results.add(readRecord());
        } else {
            throw new MessageParserException(MessageParserException.BAD_TRANSMISSION_START,
                    String.format("(got 0x%02x)", (int)c), parseIndex, currentClass);
        }
        // expect that the transmission ends here! TODO: exception if not
        return results;
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
            throw new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, scannedToken, parseIndex, currentClass);
        }
        return value;
    }
}
