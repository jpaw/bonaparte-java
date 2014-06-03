package de.jpaw.bonaparte.core;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteTestsASCII;

public class CompactParser extends CompactConstants implements MessageParser<IOException> {
    
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
        int d = in.readUnsignedByte();
        if (c != d) {
            throw new IOException(String.format("expected 0x%02x, got 0x%02x in class %s", (int)c, (int)d, currentClass));
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean allowNull) throws IOException {
        int c = needToken();
        if (c == NULL_FIELD) {
            if (allowNull) {
                return true;
            } else {
                throw new IOException("Null not allowed for " + currentClass + "." + fieldname + " (found explicit null token)");
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == COLLECTIONS_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (allowNull) {
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

    @Override
    public BigDecimal readBigDecimal(String fieldname, boolean allowNull, int length, int decimals, boolean isSigned, boolean rounding, boolean autoScale) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Character readCharacter(String fieldname, boolean allowNull) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID readUUID(String fieldname, boolean allowNull) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean readBoolean(String fieldname, boolean allowNull) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double readDouble(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float readFloat(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long readLong(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer readInteger(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Short readShort(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte readByte(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer readNumber(String fieldname, boolean allowNull, int length, boolean isSigned) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String readAscii(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String readString(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteArray readByteArray(String fieldname, boolean allowNull, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] readRaw(String fieldname, boolean allowNull, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate readDay(String fieldname, boolean allowNull) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDateTime readDayTime(String fieldname, boolean allowNull, boolean hhmmss, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalTime readTime(String fieldname, boolean allowNull, boolean hhmmss, int length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BonaPortable readObject(String fieldname, Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int parseMapStart(String fieldname, boolean allowNull, int indexID) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int parseArrayStart(String fieldname, boolean allowNull, int max, int sizeOfElement) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void parseArrayEnd() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public BonaPortable readRecord() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BonaPortable> readTransmission() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setClassName(String newClassName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void eatParentSeparator() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public IOException enumExceptionConverter(IllegalArgumentException e) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

//    private void skipLeadingSpaces() {
//        while (parseIndex < messageLength) {
//            byte c = inputdata[parseIndex];
//            if ((c != ' ') && (c != '\t')) {
//                break;
//            }
//            // skip leading blanks
//            ++parseIndex;
//        }
//    }
//
//    private void skipNulls() {
//        while (parseIndex < messageLength) {
//            byte c = inputdata[parseIndex];
//            if (c != NULL_FIELD) {
//                break;
//            }
//            // skip trailing NULL objects
//            ++parseIndex;
//        }
//    }
//
//    private String nextIndexParseAscii(String fieldname, boolean allowSign, boolean allowDecimalPoint, boolean allowExponent) throws IOException {
//        final int BUFFER_SIZE = 40;
//        boolean allowSignNextIteration = false;
//        boolean gotAnyDigit = false;
//        StringBuffer tmp = new StringBuffer(BUFFER_SIZE);
//
//        // skipBlanks: does not hurt!
//        skipLeadingSpaces();
//        if ((parseIndex < messageLength) && (inputdata[parseIndex] == PLUS_SIGN)) {
//            // allow positive sign in any case (but not followed by a minus)
//            ++parseIndex;
//            allowSign = false;
//        }
//        while (parseIndex < messageLength) {
//            byte c = inputdata[parseIndex];
//            if (c == FIELD_TERMINATOR) {
//                if (!gotAnyDigit) {
//                    throw new IOException(IOException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
//                }
//                ++parseIndex;  // eat it!
//                return tmp.toString();
//            }
//
//            if (c == MINUS_SIGN) {
//                if (!allowSign) {
//                    throw new IOException(IOException.SUPERFLUOUS_SIGN, fieldname, parseIndex, currentClass);
//                }
//            } else if (c == DECIMAL_POINT) {
//                if (!allowDecimalPoint) {
//                    throw new IOException(IOException.SUPERFLUOUS_DECIMAL_POINT, fieldname, parseIndex, currentClass);
//                }
//                allowDecimalPoint = false;  // no 2 in a row allowed
//            } else if ((c == 'e') || (c == 'E')) {
//                if (!allowExponent) {
//                    throw new IOException(IOException.SUPERFLUOUS_EXPONENT, fieldname, parseIndex, currentClass);
//                }
//                if (!gotAnyDigit) {
//                    throw new IOException(IOException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
//                }
//                allowSignNextIteration = true;
//                allowExponent = false;
//                allowDecimalPoint = false;
//            } else if (ByteTestsASCII.isAsciiDigit(c)) {
//                gotAnyDigit = true;
//            } else {
//                throw new IOException(IOException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
//            }
//            if (tmp.length() >= BUFFER_SIZE) {
//                throw new IOException(IOException.NUMERIC_TOO_LONG, fieldname, parseIndex, currentClass);
//            }
//            tmp.appendCodePoint(c);
//            ++parseIndex;
//            allowSign = allowSignNextIteration;
//            allowSignNextIteration = false;
//        }
//        // end of message without appropriate terminator character
//        throw new IOException(IOException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
//    }
//
//    @Override
//    public BigDecimal readBigDecimal(String fieldname, boolean allowNull, int length, int decimals, boolean isSigned, boolean rounding, boolean autoScale) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        BigDecimal r = new BigDecimal(nextIndexParseAscii(fieldname, isSigned, true, false));
//        try {
//            if (r.scale() > decimals)
//                r = r.setScale(decimals, rounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
//            if (autoScale && r.scale() < decimals)  // round for smaller as well!
//                r = r.setScale(decimals, RoundingMode.UNNECESSARY);
//        } catch (ArithmeticException a) {
//            throw new IOException(IOException.TOO_MANY_DECIMALS, fieldname, parseIndex, currentClass);
//        }
//        return r;
//    }
//
//    @Override
//    public Character readCharacter(String fieldname, boolean allowNull) throws IOException {
//        String tmp = readString(fieldname, allowNull, 1, false, false, true, true);
//        if (tmp == null) {
//            return null;
//        }
//        if (tmp.length() == 0) {
//            throw new IOException(IOException.EMPTY_CHAR, fieldname, parseIndex, currentClass);
//        }
//        return tmp.charAt(0);
//    }
//
//    // readString does the job for Unicode as well as ASCII, but only used for Unicode (have an optimized version for ASCII)
//    @Override
//    public String readString(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        if (doTrim) {
//            // skip leading spaces
//            skipLeadingSpaces();
//        }
//        // parse the data and check for escape sequences
//        boolean escapeUsed = false;
//        int currentIndex = parseIndex;
//        int lastNonBlank = parseIndex-1;
//        while (currentIndex < messageLength) {
//            byte b = inputdata[currentIndex];
//            if (b == FIELD_TERMINATOR) {
//                // regular end of string
//                String result;
//                if (!doTrim) {
//                    // ignore last nonblank
//                    lastNonBlank = currentIndex-1;
//                }
//                if (lastNonBlank < parseIndex) {
//                    // zero character string
//                    result = EMPTY_STRING;
//                } else if (!escapeUsed) {
//                    // simple (& fast?) way to do this
//                    result = new String(inputdata, parseIndex, (lastNonBlank-parseIndex)+1, getCharset());
//                } else if (!allowCtrls || !allowUnicode) {
//                    // check if escapes were allowed in the first place...   NO!
//                    throw new IOException(IOException.ILLEGAL_CHAR_CTRL, fieldname, parseIndex, currentClass);
//                } else {
//                    // the ugly part. Must run through it again, replacing
//                    // escapes, need a temporary buffer
//                    byte[] tmp = new byte[(lastNonBlank - parseIndex) + 1]; // preliminary
//                    // length,
//                    // escapes
//                    // will
//                    // reduce
//                    // this
//                    int i = 0;
//                    while (parseIndex <= lastNonBlank) {
//                        b = inputdata[parseIndex++];
//                        if (b == ESCAPE_CHAR) {
//                            b = inputdata[parseIndex++];
//                            if ((b < 0x40) || (b >= 0x60)) {
//                                throw new IOException(IOException.ILLEGAL_ESCAPE_SEQUENCE,
//                                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
//                            }
//                            b -= 0x40;
//                        }
//                        tmp[i++] = b;
//                    }
//                    result = new String(tmp, 0, i, getCharset());
//                }
//                parseIndex = currentIndex + 1;
//                // length checks
//                if (length > 0) {
//                    // have limits on max size
//                    if (result.length() > length) {
//                        if (doTruncate) {
//                            result = result.substring(0, length);
//                        } else {
//                            throw new IOException(IOException.STRING_TOO_LONG,
//                                    String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, result.toString()),
//                                    parseIndex, currentClass);
//                        }
//                    }
//                }
//                return result;
//            }
//            if (b == ESCAPE_CHAR) {
//                escapeUsed = true;
//            } else if ((b != ' ') && (b != '\t')) {
//                lastNonBlank = currentIndex;
//            } else if (!allowUnicode) {
//                if (!ByteTestsASCII.isAsciiPrintable(b) && (b != '\t')) {
//                    throw new IOException(IOException.ILLEGAL_CHAR_ASCII,
//                            String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
//                }
//            }
//            ++currentIndex;
//        }
//        throw new IOException(IOException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
//    }
//
//    // specialized version without charset conversion
//    @Override
//    public String readAscii(String fieldname, boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        if (doTrim) {
//            // skip leading spaces
//            skipLeadingSpaces();
//        }
//        StringBuilder tmp = new StringBuilder(length);
//        while (parseIndex < messageLength) {
//            byte b = inputdata[parseIndex++];
//            if (b == FIELD_TERMINATOR) {
//                // regular end of string. Check possible trimming
//                int resultLength = tmp.length();
//                if (doTrim) {
//                    int lastNonBlank = resultLength;
//                    char c;
//                    while ((lastNonBlank > 0)
//                            && (((c = tmp.charAt(lastNonBlank-1)) ==  ' ') || (c == '\t'))) {
//                        --lastNonBlank;
//                    }
//                    if (lastNonBlank < resultLength) {
//                        resultLength = lastNonBlank;
//                        tmp.setLength(lastNonBlank);
//                    }
//                }
//                if ((length > 0) && (resultLength > length)) {
//                    if (doTruncate) {
//                        tmp.setLength(length);
//                    } else {
//                        throw new IOException(IOException.STRING_TOO_LONG,
//                                String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, tmp.toString()),
//                                parseIndex, currentClass);
//                    }
//                }
//                if (resultLength == 0) {
//                    return EMPTY_STRING;
//                } else {
//                    return tmp.toString();
//                }
//            }
//            if (b == ESCAPE_CHAR) {
//                throw new IOException(IOException.ILLEGAL_CHAR_CTRL, fieldname, parseIndex, currentClass);
//            } else if (!ByteTestsASCII.isAsciiPrintable(b) && (b != '\t')) {
//                throw new IOException(IOException.ILLEGAL_CHAR_ASCII,
//                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
//            }
//            tmp.append((char)b);
//        }
//        throw new IOException(IOException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
//    }
//
//    @Override
//    public Boolean readBoolean(String fieldname, boolean allowNull) throws IOException {
//        boolean result;
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        byte c = needToken();
//        if (c == '0') {
//            result = false;
//        } else if (c == '1') {
//            result = true;
//        } else {
//            throw new IOException(IOException.ILLEGAL_BOOLEAN,
//                    String.format("(found 0x%02x for %s)", (int)c, fieldname), parseIndex, currentClass);
//        }
//        needToken(FIELD_TERMINATOR);
//        return result;
//    }
//
//    @Override
//    public ByteArray readByteArray(String fieldname, boolean allowNull, int length) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        skipLeadingSpaces();
//        // compute length of data and perform (rough) check for illegal characters
//        int i = parseIndex;
//        // find next occurence of field terminator
//        while (i < messageLength) {
//            byte b = inputdata[i++];
//            if (b == FIELD_TERMINATOR) {
//                ByteArray result = ByteArray.fromBase64(inputdata, parseIndex, i-parseIndex-1);
//                if (result == null) {
//                    throw new IOException(IOException.BASE64_PARSING_ERROR, fieldname, parseIndex, currentClass);
//                }
//                parseIndex = i;
//                return result;
//            }
//            if ((b >= '0') && (b <= 'z')) {
//                ; // OK
//            } else if ((b == '+') || (b == '/') || (b == '=')) {
//                ; // OK
//            } else {
//                throw new IOException(IOException.ILLEGAL_CHAR_BASE64,
//                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
//            }
//        }
//        throw new IOException(IOException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
//    }
//
//    @Override
//    public byte[] readRaw(String fieldname, boolean allowNull, int length) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        skipLeadingSpaces();
//        // compute length of data and perform (rough) check for illegal characters
//        int i = parseIndex;
//        // find next occurence of field terminator
//        while (i < messageLength) {
//            byte b = inputdata[i++];
//            if (b == FIELD_TERMINATOR) {
//                byte [] result;
//                // have the subset of data
//                if (i == (parseIndex+1)) {
//                    // zero length
//                    result = new byte[0];
//                } else {
//                    result = Base64.decode(inputdata, parseIndex, i-parseIndex-1);
//                    if (result == null) {
//                        throw new IOException(IOException.BASE64_PARSING_ERROR, fieldname, parseIndex, currentClass);
//                    }
//                }
//                parseIndex = i;
//                return result;
//            }
//            if ((b >= '0') && (b <= 'z')) {
//                ; // OK
//            } else if ((b == '+') || (b == '/') || (b == '=')) {
//                ; // OK
//            } else {
//                throw new IOException(IOException.ILLEGAL_CHAR_BASE64,
//                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
//            }
//        }
//        throw new IOException(IOException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
//    }
//
//    @Override
//    public LocalDateTime readDayTime(String fieldname, boolean allowNull, boolean hhmmss, int fractionalDigits) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        String tmp = nextIndexParseAscii(fieldname, false, fractionalDigits >= 0, false);  // parse an unsigned numeric string without exponent
//        int date;
//        int fractional = 0;
//        int dpoint;
//        if ((dpoint = tmp.indexOf('.')) < 0) {
//            // day only despite allowed time
//            date = Integer.parseInt(tmp);
//        } else {
//            // day and time
//            date = Integer.parseInt(tmp.substring(0, dpoint));
//            fractional = Integer.parseInt(tmp.substring(dpoint + 1));
//            switch (tmp.length() - dpoint - 1) { // i.e. number of fractional digits
//            case 6:
//                fractional *= 1000;
//                break; // precisely seconds resolution (timestamp(0))
//            case 7:
//                fractional *= 100;
//                break;
//            case 8:
//                fractional *= 10;
//                break;
//            case 9:
//                break; // maximum resolution (milliseconds)
//            default: // something weird
//                throw new IOException(
//                        IOException.BAD_TIMESTAMP_FRACTIONALS,
//                        String.format("(found %d for %s)", tmp.length() - dpoint - 1, fieldname),
//                        parseIndex, currentClass);
//            }
//        }
//        // set the date and time
//        int day, month, year, hour, minute, second;
//        year = date / 10000;
//        month = (date %= 10000) / 100;
//        day = date %= 100;
//        if (hhmmss) {
//            hour = fractional / 10000000;
//            minute = (fractional %= 10000000) / 100000;
//            second = (fractional %= 100000) / 1000;
//        } else {
//            hour = fractional / 3600000;
//            minute = (fractional %= 3600000) / 60000;
//            second = (fractional %= 60000) / 1000;
//        }
//        fractional %= 1000;
//        // first checks
//        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
//                || (day > 31)) {
//            throw new IOException(
//                    IOException.ILLEGAL_DAY, String.format(
//                            "(found %d for %s)", date, fieldname), parseIndex, currentClass);
//        }
//        if ((hour > 23) || (minute > 59) || (second > 59)) {
//            throw new IOException(
//                    IOException.ILLEGAL_TIME,
//                    String.format("(found %d for %s)", (hour * 10000) + (minute * 100)
//                            + second, fieldname), parseIndex, currentClass);
//        }
//        // now set the return value
//        LocalDateTime result;
//        try {
//            // TODO! default is lenient mode, therefore will not check. Solution
//            // is to read the data again and compare the values of day, month
//            // and year
//            result = new LocalDateTime(year, month, day, hour, minute, second, fractional);
//        } catch (Exception e) {
//            throw new IOException(
//                    IOException.ILLEGAL_CALENDAR_VALUE, fieldname,
//                    parseIndex, currentClass);
//        }
//        return result;
//    }
//    @Override
//    public LocalDate readDay(String fieldname, boolean allowNull) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        String tmp = nextIndexParseAscii(fieldname, false, false, false);  // parse an unsigned numeric string without exponent
//        int date = Integer.parseInt(tmp);
//        // set the date and time
//        int day, month, year;
//        year = date / 10000;
//        month = (date %= 10000) / 100;
//        day = date %= 100;
//        // first checks
//        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
//                || (day > 31)) {
//            throw new IOException(
//                    IOException.ILLEGAL_DAY, String.format(
//                            "(found %d for %s)", date, fieldname), parseIndex, currentClass);
//        }
//        // now set the return value
//        LocalDate result;
//        try {
//            // TODO! default is lenient mode, therefore will not check. Solution
//            // is to read the data again and compare the values of day, month
//            // and year
//            result = new LocalDate(year, month, day);
//        } catch (Exception e) {
//            throw new IOException(IOException.ILLEGAL_CALENDAR_VALUE, fieldname,  parseIndex, currentClass);
//        }
//        return result;
//    }
//
//    @Override
//    public int parseMapStart(String fieldname, boolean allowNull, int indexID) throws IOException {
//        if (checkForNull(fieldname, true)) {  // check it separately in order to give a distinct error message
//            if (!allowNull)
//                throw new IOException(IOException.NULL_MAP_NOT_ALLOWED_HERE, fieldname, parseIndex, currentClass);
//            return -1;
//        }
//        needToken(MAP_BEGIN);
//        int foundIndexType = readInteger(fieldname, false, false);
//        if (foundIndexType != indexID) {
//            throw new IOException(IOException.WRONG_MAP_INDEX_TYPE,
//                    String.format("(got %d, expected for %s)", foundIndexType, indexID, fieldname), parseIndex, currentClass);
//        }
//        int n = readInteger(fieldname, false, false);
//        if ((n < 0) || (n > 1000000000)) {
//            throw new IOException(IOException.ARRAY_SIZE_OUT_OF_BOUNDS,
//                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
//        }
//        return n;
//    }
//
//    @Override
//    public int parseArrayStart(String fieldname, boolean allowNull, int max, int sizeOfChild) throws IOException {
//        if (checkForNull(fieldname, true)) {
//            if (!allowNull)
//                throw new IOException(IOException.NULL_COLLECTION_NOT_ALLOWED, fieldname, parseIndex, currentClass);
//            return -1;
//        }
//        needToken(ARRAY_BEGIN);
//        int n = readInteger(fieldname, false, false);
//        if ((n < 0) || (n > 1000000000)) {
//            throw new IOException(IOException.ARRAY_SIZE_OUT_OF_BOUNDS,
//                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
//        }
//        return n;
//    }
//
//    @Override
//    public void parseArrayEnd() throws IOException {
//        needToken(COLLECTIONS_TERMINATOR);
//
//    }
//
//    @Override
//    public BonaPortable readRecord() throws IOException {
//        BonaPortable result;
//        needToken(RECORD_BEGIN);
//        needToken(NULL_FIELD); // version no
//        result = readObject(GENERIC_RECORD, BonaPortable.class, false, true);
//        skipByte(RECORD_OPT_TERMINATOR);
//        needToken(RECORD_TERMINATOR);
//        return result;
//    }
//
//    @Override
//    public Byte readByte(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Byte.valueOf(nextIndexParseAscii(fieldname, isSigned, false, false));
//    }
//
//    @Override
//    public Short readShort(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Short.valueOf(nextIndexParseAscii(fieldname, isSigned, false, false));
//    }
//
//    @Override
//    public Long readLong(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Long.valueOf(nextIndexParseAscii(fieldname, isSigned, false, false));
//    }
//
//    @Override
//    public Integer readInteger(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Integer.valueOf(nextIndexParseAscii(fieldname, isSigned, false, false));
//    }
//
//    @Override
//    public Float readFloat(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Float.valueOf(nextIndexParseAscii(fieldname, isSigned, true, true));
//    }
//
//    @Override
//    public Double readDouble(String fieldname, boolean allowNull, boolean isSigned) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        return Double.valueOf(nextIndexParseAscii(fieldname, isSigned, true, true));
//    }
//
//    @Override
//    public void eatParentSeparator() throws IOException {
//        eatObjectOrParentSeparator(PARENT_SEPARATOR);
//    }       
//        
//    public void eatObjectTerminator() throws IOException {
//        eatObjectOrParentSeparator(OBJECT_TERMINATOR);
//    }
//    
//    protected void eatObjectOrParentSeparator(byte which) throws IOException {
//        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
//        byte z = needToken();
//        if (z == which)
//            return;   // all good
//        
//        // temporarily provide compatibility to 1.7.9 and back...
//        if (z == PARENT_SEPARATOR) {
//            // implies we have been looking for OBJECT_TERMINATOR...
//            return;
//        }
//        
//        // we have extra data and it is not null. Now the behavior depends on a parser setting
//        ParseSkipNonNulls mySetting = getSkipNonNullsBehavior();
//        switch (mySetting) {
//        case ERROR:
//            throw new IOException(IOException.EXTRA_FIELDS, String.format("(found byte 0x%02x)", z), parseIndex, currentClass);  
//        case WARN:
//            LOGGER.warn("{} at index {} parsing class {}", IOException.codeToString(IOException.EXTRA_FIELDS), parseIndex, currentClass);
//            // fall through
//        case IGNORE:
//            // skip bytes until we are at end of record (bad!) (thrown by needToken()) or find the terminator
//            skipUntilNext(which);
//        }
//    }
//    
//    protected void skipUntilNext(byte which) throws IOException {
//        byte c;
//        while ((c = needToken()) != which) {
//            if (c == OBJECT_BEGIN) {
//                // skip nested object!
//                skipUntilNext(OBJECT_TERMINATOR);
//            }
//        }
//    }
//
//    @Override
//    public Integer readNumber(String fieldname, boolean allowNull, int length, boolean isSigned)
//            throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        String tmp = nextIndexParseAscii(fieldname, isSigned, false, false);
//        if (tmp.length() > (length + (((tmp.charAt(0) == '-') || (tmp.charAt(0) == '+')) ? 1 : 0))) {
//            throw new IOException(IOException.NUMERIC_TOO_LONG,
//                    String.format("(allowed %d, found %d for %s)", length, tmp.length(), fieldname), parseIndex, currentClass);
//        }
//        return Integer.valueOf(tmp);
//    }
//
//    @Override
//    public BonaPortable readObject(String fieldname, Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws IOException {
//        if (checkForNull(fieldname, allowNull)) {
//            return null;
//        }
//        if (useCache && parseIndex < messageLength && inputdata[parseIndex] == OBJECT_AGAIN) {
//            // we reuse an object
//            ++parseIndex;
//            int objectIndex = readInteger(fieldname, false, false).intValue();
//            if (objectIndex >= objects.size())
//                throw new IOException(IOException.INVALID_BACKREFERENCE, String.format(
//                        "at %s: requested object %d of only %d available", fieldname, objectIndex, objects.size()),
//                        parseIndex, currentClass);
//            BonaPortable newObject = objects.get(objects.size() - 1 - objectIndex);  // 0 is the last one put in, 1 the one before last etc...
//            // check if the object is of expected type
//            if (newObject.getClass() != type) {
//                // check if it is a superclass
//                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
//                    throw new IOException(IOException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
//                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
//                }
//            }
//            return newObject;
//        } else {
//            String previousClass = currentClass;
//            needToken(OBJECT_BEGIN); // version not yet allowed
//            String classname = readString(fieldname, false, 0, false, false, false, false);
//            // String revision = readAscii(true, 0, false, false);
//            needToken(NULL_FIELD); // version not yet allowed
//            BonaPortable newObject = BonaPortableFactory.createObject(classname);
//            // System.out.println("Creating new obj " + classname + " gave me " + newObject);
//            // check if the object is of expected type
//            if (newObject.getClass() != type) {
//                // check if it is a superclass
//                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
//                    throw new IOException(IOException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
//                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
//                }
//            }
//            // all good here. Parse the contents
//            // if we use the cache, make the object known even before the contents has been parsed, because it may be referenced if the structure is cyclic
//            if (useCache)
//                objects.add(newObject);
//            
//            currentClass = classname;
//            newObject.deserialize(this);
//            eatObjectTerminator();
//            currentClass = previousClass;
//            return newObject;
//        }
//    }
//
//    @Override
//    public List<BonaPortable> readTransmission() throws IOException {
//        List<BonaPortable> results = new ArrayList<BonaPortable>();
//        byte c = needToken();
//        if (c == TRANSMISSION_BEGIN) {
//            needToken(NULL_FIELD);  // version
//            // TODO: parse extensions here
//            while ((c = needToken()) != TRANSMISSION_TERMINATOR) {
//                // System.out.println("transmission loop: char is " + c);
//                --parseIndex; // push back object def
//                results.add(readRecord());
//            }
//            // when here, last char was transmission terminator
//            // optionally eat the last one as well?
//        } else if (c == RECORD_BEGIN /* || c == EXTENSION_BEGIN */) {
//            // allow single record as a special case
//            // TODO: parse extensions here
//            --parseIndex;
//            results.add(readRecord());
//        } else {
//            throw new IOException(IOException.BAD_TRANSMISSION_START,
//                    String.format("(got 0x%02x)", (int)c), parseIndex, currentClass);
//        }
//        // expect that the transmission ends here! TODO: exception if not
//        return results;
//    }
//
//
//    @Override
//    public UUID readUUID(String fieldname, boolean allowNull) throws IOException {
//        String tmp = readAscii(fieldname, allowNull, 36, true, false);
//        if (tmp == null) {
//            return null;
//        }
//        try {
//            return UUID.fromString(tmp);
//        } catch (IllegalArgumentException e) {
//            throw new IOException(IOException.BAD_UUID_FORMAT, tmp, parseIndex, currentClass);
//        }
//    }
//
//
//    @Override
//    public IOException enumExceptionConverter(IllegalArgumentException e) {
//        return new IOException(IOException.INVALID_ENUM_TOKEN, e.getMessage(), parseIndex, currentClass);
//    }
//
//
//    @Override
//    public void setClassName(String newClassName) {
//        currentClass = newClassName;
//    }
//
//
//    @Override
//    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws IOException {
//        XEnumDefinition spec = di.getBaseXEnum();
//        String scannedToken = readString(di.getName(), !di.getIsRequired() || spec.getHasNullToken(), spec.getMaxTokenLength(), true, false, false, true);
//        if (scannedToken == null)
//            return factory.getNullToken();
//        T value = factory.getByToken(scannedToken);
//        if (value == null) {
//            throw new IOException(IOException.INVALID_ENUM_TOKEN, scannedToken, parseIndex, currentClass);
//        }
//        return value;
//    }

}
