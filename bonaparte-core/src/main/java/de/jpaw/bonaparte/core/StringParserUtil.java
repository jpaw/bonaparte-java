package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.function.LongFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.bonaparte.util.DayTime;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
import de.jpaw.util.IntegralLimits;

/** Utility subroutines, to be used by various parsers.
 * This class performs the logical parsing, while leaving the splitting of fields to the callers.
 *
 * All parameters are expected to be not-null, null checks must happen before.
 *
 */
public class StringParserUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringParserUtil.class);
    private final ParsePositionProvider parsePositionProvider;
    private final boolean instantInMillis;

    public StringParserUtil() {
        parsePositionProvider = ParsePositionProvider.DEFAULT;
        instantInMillis = false;
    }

    public StringParserUtil(ParsePositionProvider parsePositionProvider) {
        this.parsePositionProvider = parsePositionProvider;
        instantInMillis = false;
    }

    public StringParserUtil(ParsePositionProvider parsePositionProvider, boolean instantInMillis) {
        this.parsePositionProvider = parsePositionProvider;
        this.instantInMillis = instantInMillis;
    }

    protected MessageParserException err(int errno, FieldDefinition di, String data) {
        return new MessageParserException(errno, di.getName(), data, parsePositionProvider);
    }

    public void ensureNotNull(FieldDefinition di, String data) throws MessageParserException {
        if (data == null)
            throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, di.getName(), null, parsePositionProvider);
    }

    public Character readCharacter(final MiscElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        if (data.length() == 0) {
            throw err(MessageParserException.EMPTY_CHAR, di, data);
        } else if (data.length() > 1) {
            throw err(MessageParserException.CHAR_TOO_LONG, di, data);
        }
        return Character.valueOf(data.charAt(0));
    }

    // just do trim and length checks
    protected String readStringSub(final AlphanumericElementaryDataItem di, String data) throws MessageParserException {
        if (di.getDoTrim())
            data = data.trim();
        if (data.length() > di.getLength()) {
            if (di.getDoTruncate())
                data = data.substring(0, di.getLength());
            else
                throw err(MessageParserException.STRING_TOO_LONG, di, data);
        } else if (data.length() < di.getMinLength()) {
            throw err(MessageParserException.STRING_TOO_SHORT, di, data);
        }
        return data;
    }

    public String readAscii(final AlphanumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        data = readStringSub(di, data);
        // ASCII checks
        String type = di.getDataType().toLowerCase();
        if (type.equals("lower") && !CharTestsASCII.isLowerCase(data))
            throw err(MessageParserException.ILLEGAL_CHAR_LOWER, di, data);
        else if (type.equals("upper") && !CharTestsASCII.isUpperCase(data))
            throw err(MessageParserException.ILLEGAL_CHAR_UPPER, di, data);
        else {
            if (!CharTestsASCII.isPrintable(data))
                throw err(MessageParserException.ILLEGAL_CHAR_ASCII, di, data);
        }
        return data;
    }
    // readString does the job for Unicode as well as ASCII
    public String readString(final AlphanumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        data = readStringSub(di, data);
        // check for escape chars (unescaping is done before calling this method, as it is protocol dependent!
        for (int i = 0; i < data.length(); ++i) {
            char c = data.charAt(i);
            if (c < ' ' && c != '\t' && !di.getAllowControlCharacters())
                throw err(MessageParserException.ILLEGAL_CHAR_CTRL, di, data);
        }
        return data;
    }

    public Boolean readBoolean(final MiscElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        if (data.equals("false"))
            return Boolean.FALSE;
        if (data.equals("true"))
            return Boolean.TRUE;
        if (data.length() != 1)
            throw err(MessageParserException.ILLEGAL_BOOLEAN, di, data);
        final char c = data.charAt(0);
        if (c == '0') {
            return Boolean.FALSE;
        } else if (c == '1') {
            return Boolean.TRUE;
        } else {
            throw err(MessageParserException.ILLEGAL_BOOLEAN, di, data);
        }
    }

    public ByteArray readByteArray(BinaryElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        if (data.length() % 4 != 0)
            throw err(MessageParserException.BASE64_PARSING_ERROR, di, data);
        if ((data.length() / 4) * 3 > di.getLength())
            throw err(MessageParserException.BINARY_TOO_LONG, di, data);
        return new ByteArray(readRaw(di, data)); // TODO: this call does an unnecessary copy
    }

    public byte[] readRaw(BinaryElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        try {
            byte [] btmp = data.getBytes();
            if (btmp.length % 4 != 0)
                throw err(MessageParserException.BASE64_PARSING_ERROR, di, data);
            if ((btmp.length / 4) * 3 > di.getLength())
                throw err(MessageParserException.BINARY_TOO_LONG, di, data);
            return Base64.decode(btmp, 0, btmp.length);
        } catch (IllegalArgumentException e) {
            throw err(MessageParserException.BASE64_PARSING_ERROR, di, data);
        }
        // return DatatypeConverter.parseHexBinary(data);
    }

    public LocalDateTime readDayTime(TemporalElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        int date;
        int fractional = 0;
        int dpoint;
        if ((dpoint = data.indexOf('.')) < 0) {
            // day only despite allowed time
            date = Integer.parseInt(data);
        } else {
            // day and time
            date = Integer.parseInt(data.substring(0, dpoint));
            fractional = Integer.parseInt(data.substring(dpoint + 1));
            final int numberOfFractionals = data.length() - dpoint - 1;
            switch (numberOfFractionals) { // i.e. number of fractional digits
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
                LOGGER.error("Timestamp fractionals: {} for data {}", numberOfFractionals, data.substring(dpoint));
                throw err(MessageParserException.BAD_TIMESTAMP_FRACTIONALS, di,
                          String.format("(found %d for %s)", numberOfFractionals, data));
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
            throw err(MessageParserException.ILLEGAL_DAY, di,
                      String.format("(found %d for %s)", year*10000+month*100+day, data));
        }
        if ((hour > 23) || (minute > 59) || (second > 59)) {
            throw err(MessageParserException.ILLEGAL_TIME, di,
                      String.format("(found %d for %s)", (hour * 10000) + (minute * 100) + second, data));
        }
        // now set the return value
        LocalDateTime result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = LocalDateTime.of(year, month, day, hour, minute, second, fractional * 1000000);
        } catch (Exception e) {
            throw err(MessageParserException.ILLEGAL_CALENDAR_VALUE, di, data);
        }
        return result;
    }

    public LocalDate readDay(TemporalElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        int date = Integer.parseInt(data);
        // set the date and time
        int day, month, year;
        year = date / 10000;
        month = (date %= 10000) / 100;
        day = date %= 100;
        // first checks
        if ((year < 1601) || (year > 2399) || (month == 0) || (month > 12) || (day == 0)
                || (day > 31)) {
            throw err(MessageParserException.ILLEGAL_DAY, di,
                      String.format("(found %d for %s)", year*10000+month*100+day, data));
        }
        // now set the return value
        LocalDate result;
        try {
            // TODO! default is lenient mode, therefore will not check. Solution
            // is to read the data again and compare the values of day, month
            // and year
            result = LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw err(MessageParserException.ILLEGAL_CALENDAR_VALUE, di, data);
        }
        return result;
    }

    public LocalTime readTime(TemporalElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        int millis = 0;
        int seconds = 0;
        int dpoint;
        if ((dpoint = data.indexOf('.')) < 0) {
            seconds = Integer.parseInt(data);  // only seconds
        } else {
            // seconds and millis seconds
            seconds = Integer.parseInt(data.substring(0, dpoint));
            millis = Integer.parseInt(data.substring(dpoint + 1));
            switch (data.length() - dpoint - 1) { // i.e. number of fractional digits
            case 2:
                millis *= 10;
                break;
            case 1:
                millis *= 100;
                break;
            case 3:
                break; // maximum resolution (milliseconds)
            default: // something weird
                throw err(MessageParserException.BAD_TIMESTAMP_FRACTIONALS, di,
                          String.format("(found %d for %s)", data.length() - dpoint - 1, data));
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
            throw err(MessageParserException.ILLEGAL_TIME, di,
                      String.format("(found %d for %s)", (hour * 10000) + (minute * 100) + second, data));
        }
        return DayTime.timeForMillis(1000 * seconds + millis);
    }

    // see ExtendedJsonEscaperForAppendables.instantInMillis: this variant assumes "false", i.e. instant in seconds.
    public Instant readInstant(TemporalElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        if (instantInMillis) {
            return Instant.ofEpochMilli(Long.parseLong(data));
        }
        int millis = 0;
        long seconds = 0;
        int dpoint;
        if ((dpoint = data.indexOf('.')) < 0) {
            seconds = Long.parseLong(data);  // only seconds
        } else {
            // seconds and millis seconds
            seconds = Long.parseLong(data.substring(0, dpoint));
            millis = Integer.parseInt(data.substring(dpoint + 1));
            switch (data.length() - dpoint - 1) { // i.e. number of fractional digits
            case 2:
                millis *= 10;
                break;
            case 1:
                millis *= 100;
                break;
            case 3:
                break; // maximum resolution (milliseconds)
            default: // something weird
                throw err(MessageParserException.BAD_TIMESTAMP_FRACTIONALS, di,
                          String.format("(found %d for %s)", data.length() - dpoint - 1, data));
            }
        }
        if (di.getFractionalSeconds() == 0) {
            // don't want millis here: trunc!  (TODO: add a flag to complain!)
            millis = 0;
        }
        return Instant.ofEpochMilli(1000L * seconds + millis);
    }

    public Byte readByte(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        return Byte.valueOf(readPrimitiveByte(di, data));
    }

    public Short readShort(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        return Short.valueOf(readPrimitiveShort(di, data));
    }

    public Integer readInteger(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        return Integer.valueOf(readPrimitiveInteger(di, data));
    }

    public Long readLong(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        return Long.valueOf(readPrimitiveLong(di, data));
    }

    public Float readFloat(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        return Float.valueOf(readPrimitiveFloat(di, data));
    }

    public Double readDouble(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        return Double.valueOf(readPrimitiveDouble(di, data));
    }

    public BigInteger readBigInteger(final BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        if (data.length() > (di.getTotalDigits() + (((data.charAt(0) == '-') || (data.charAt(0) == '+')) ? 1 : 0)))
            throw err(MessageParserException.NUMERIC_TOO_LONG, di, data);
        try {
            final BigInteger r = new BigInteger(data);
            if (!di.getIsSigned() && r.signum() < 0)
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    // parse the not-null String into a BigDecimal, and perform optional scaling
    public BigDecimal readBigDecimal(final NumericElementaryDataItem di, String data) throws MessageParserException {
        if (data == null)
            return null;
        try {
            BigDecimal r = new BigDecimal(data);
            if (!di.getIsSigned() && r.signum() < 0)
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            int decimals = di.getDecimalDigits();
            try {
                if (r.scale() > decimals)
                    r = r.setScale(decimals, di.getRounding() ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
                if (di.getAutoScale() && r.scale() < decimals) // round for smaller as well!
                    r = r.setScale(decimals, RoundingMode.UNNECESSARY);
            } catch (ArithmeticException a) {
                throw err(MessageParserException.TOO_MANY_DECIMALS, di, data);
            }
            // check for overflow
            if (di.getTotalDigits() - decimals < r.precision() - r.scale())
                throw err(MessageParserException.TOO_MANY_DIGITS, di, data);
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public <F extends FixedPointBase<F>> F readFixedPoint(BasicNumericElementaryDataItem di, String data, LongFunction<F> factory) throws MessageParserException {
        if (data == null) {
            return null;
        }
        final long mantissa = FixedPointBase.mantissaFor(data, di.getDecimalDigits());
        if (!di.getIsSigned() && mantissa < 0)
            throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
        return BigDecimalTools.check(factory.apply(mantissa), di, parsePositionProvider.getParsePosition(), parsePositionProvider.getCurrentClassName());
    }

    public UUID readUUID(final MiscElementaryDataItem di, String data) throws MessageParserException {
        if (data == null) {
            return null;
        }
        try {
            return UUID.fromString(data);
        } catch (IllegalArgumentException e) {
            throw err(MessageParserException.BAD_UUID_FORMAT, di, data);
        }
    }

    public <T extends AbstractXEnumBase<T>> T readXEnum(final XEnumDataItem di, final XEnumFactory<T> factory, String data) throws MessageParserException {
        if (data == null) {
            T result = factory.getNullToken();
            if (result == null && di.getIsRequired())
                throw err(MessageParserException.EMPTY_BUT_REQUIRED_FIELD, di, data);
            return result;
        }
        final T value = factory.getByToken(data);
        if (value == null)
            throw err(MessageParserException.INVALID_ENUM_TOKEN, di, data);
        return value;
    }

    public boolean readPrimitiveBoolean(MiscElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        if (data.equals("false"))
            return false;
        if (data.equals("true"))
            return true;
        if (data.length() != 1)
            throw err(MessageParserException.ILLEGAL_BOOLEAN, di, data);
        final char c = data.charAt(0);
        if (c == '0') {
            return false;
        } else if (c == '1') {
            return true;
        } else {
            throw err(MessageParserException.ILLEGAL_BOOLEAN, di, data);
        }
    }

    public char readPrimitiveCharacter(MiscElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        if (data.length() == 0) {
            throw err(MessageParserException.EMPTY_CHAR, di, data);
        } else if (data.length() > 1) {
            throw err(MessageParserException.CHAR_TOO_LONG, di, data);
        }
        return data.charAt(0);
    }

    public double readPrimitiveDouble(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final double r = Double.parseDouble(data);
            if (!di.getIsSigned() && r < 0)
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public float readPrimitiveFloat(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final float r = Float.parseFloat(data);
            if (!di.getIsSigned() && r < 0)
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public long readPrimitiveLong(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final long r = Long.parseLong(data);
            if (r < 0 && !di.getIsSigned())
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            final int maxDigits = di.getTotalDigits();
            if (maxDigits > 0) {
                // make sure that the parsed value does not exceed the configured number of digits
                if (r < IntegralLimits.LONG_MIN_VALUES[maxDigits] || r > IntegralLimits.LONG_MAX_VALUES[maxDigits])
                    throw err(MessageParserException.NUMERIC_TOO_MANY_DIGITS, di, data);
            }
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public int readPrimitiveInteger(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final int r = Integer.parseInt(data);
            if (r < 0 && !di.getIsSigned())
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            final int maxDigits = di.getTotalDigits();
            if (maxDigits > 0) {
                // make sure that the parsed value does not exceed the configured number of digits
                if (r < IntegralLimits.INT_MIN_VALUES[maxDigits] || r > IntegralLimits.INT_MAX_VALUES[maxDigits])
                    throw err(MessageParserException.NUMERIC_TOO_MANY_DIGITS, di, data);
            }
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public short readPrimitiveShort(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final short r = Short.parseShort(data);
            if (r < 0 && !di.getIsSigned())
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            final int maxDigits = di.getTotalDigits();
            if (maxDigits > 0) {
                // make sure that the parsed value does not exceed the configured number of digits
                if (r < IntegralLimits.SHORT_MIN_VALUES[maxDigits] || r > IntegralLimits.SHORT_MAX_VALUES[maxDigits])
                    throw err(MessageParserException.NUMERIC_TOO_MANY_DIGITS, di, data);
            }
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }

    public byte readPrimitiveByte(BasicNumericElementaryDataItem di, String data) throws MessageParserException {
        ensureNotNull(di, data);
        try {
            final byte r = Byte.parseByte(data);
            if (r < 0 && !di.getIsSigned())
                throw err(MessageParserException.SUPERFLUOUS_SIGN, di, data);
            final int maxDigits = di.getTotalDigits();
            if (maxDigits > 0) {
                // make sure that the parsed value does not exceed the configured number of digits
                if (r < IntegralLimits.BYTE_MIN_VALUES[maxDigits] || r > IntegralLimits.BYTE_MAX_VALUES[maxDigits])
                    throw err(MessageParserException.NUMERIC_TOO_MANY_DIGITS, di, data);
            }
            return r;
        } catch (NumberFormatException e) {
            throw err(MessageParserException.NUMBER_PARSING_ERROR, di, data);
        }
    }
}
