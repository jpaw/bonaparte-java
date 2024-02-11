package de.jpaw.bonaparte.mfcobol;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;
import java.util.function.ToLongFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableFactory;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.Settings;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;

public class MfcobolParser extends Settings implements MessageParser<MessageParserException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MfcobolParser.class);

    final protected Charset charset;
    private int parseIndex;
    private int messageLength;
    private byte [] inputdata;
    private String currentClass;

    protected String getClassName() {
        return null;
    }

    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src, int offset, int length) {
        inputdata = src;
        parseIndex = offset;
        messageLength = length < 0 ? src.length : length;
    }

    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src) {
        inputdata = src;
        parseIndex = 0;
        messageLength = src.length;
    }

    /** Create a processor for parsing a buffer. */
    public MfcobolParser(byte [] buffer, int offset, int length, Charset charset) {
        super();
        inputdata = buffer;
        parseIndex = offset;
        messageLength = length < 0 ? inputdata.length : length; // -1 means full array size / until end of data
        this.charset = charset;
        currentClass = "N/A";
    }

    protected int getParseIndex() {
        return parseIndex;
    }

    /**************************************************************************************************
     * Deserialization goes here. Code below does not use the ByteBuilder class,
     * but reads from the byte[] directly
     **************************************************************************************************/

    protected MessageParserException newMPE(final int errorCode, final String msg) {
        return new MessageParserException(errorCode, msg, parseIndex, currentClass);
    }

//    @Override
//    protected BonaPortable createObject(String classname) throws MessageParserException {           // same method - overloading required for possible exception mapping
//        return BonaPortableFactory.createObject(classname);
//    }
//
//    @Override
//    protected BigDecimal checkAndScale(BigDecimal num, NumericElementaryDataItem di) throws MessageParserException {
//        return BigDecimalTools.checkAndScale(num, di, parseIndex, currentClass);
//    }

    // special method only in the ByteArray version
    protected void require(int length) throws MessageParserException {
        if (parseIndex + length > messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, null);
        }
    }

    protected int needToken() throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, null);
        }
        return inputdata[parseIndex++] & 0xff;
    }

    protected int readFixed1ByteInt() throws MessageParserException {
        require(1);
        return inputdata[parseIndex++];
    }

    protected int readFixed2ByteInt() throws MessageParserException {
        require(2);
        int nn = inputdata[parseIndex++] << 8;
        return nn | inputdata[parseIndex++] & 0xff;
    }

    protected int readFixed3ByteInt() throws MessageParserException {
        require(3);
        int nn = inputdata[parseIndex++] << 16;             // does sign-extend as required
        nn |= (inputdata[parseIndex++] & 0xff) << 8;
        nn |= inputdata[parseIndex++] & 0xff;
        return nn;
    }

    protected int readFixed4ByteInt() throws MessageParserException {
        require(4);
        int nn = (inputdata[parseIndex++] & 0xff) << 24;
        nn |= (inputdata[parseIndex++] & 0xff) << 16;
        nn |= (inputdata[parseIndex++] & 0xff) << 8;
        nn |= inputdata[parseIndex++] & 0xff;
        return nn;
    }

    protected long readFixed1ByteLong() throws MessageParserException {
        return readFixed1ByteInt();
    }
    protected long readFixed2ByteLong() throws MessageParserException {
        return readFixed2ByteInt();
    }
    protected long readFixed3ByteLong() throws MessageParserException {
        return readFixed3ByteInt();
    }
    protected long readFixed4ByteLong() throws MessageParserException {
        return readFixed4ByteInt();
    }
    protected long readFixed5ByteLong() throws MessageParserException {
        require(5);
        int nn1 = inputdata[parseIndex++] & 0xff;
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }

    protected long readFixed6ByteLong() throws MessageParserException {
        require(6);
        int nn1 = inputdata[parseIndex++] << 8;
        nn1 |= inputdata[parseIndex++] & 0xff;
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }

    protected long readFixed7ByteLong() throws MessageParserException {
        int nn1 = readFixed3ByteInt();
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }

    protected long readFixed8ByteLong() throws MessageParserException {
        int nn1 = readFixed4ByteInt();
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }

    private static final ToIntFunction<MfcobolParser> [] INTARR = new ToIntFunction[5];
    static {
        INTARR[0] = r -> 0;
        INTARR[1] = r -> r.readFixed1ByteInt();
        INTARR[2] = r -> r.readFixed2ByteInt();
        INTARR[3] = r -> r.readFixed3ByteInt();
        INTARR[4] = r -> r.readFixed4ByteInt();
    };

    private static final ToLongFunction<MfcobolParser> [] LLARR = new ToLongFunction[9];
    static {
        LLARR[0] = r -> 0L;
        LLARR[1] = r -> r.readFixed1ByteLong();
        LLARR[2] = r -> r.readFixed2ByteLong();
        LLARR[3] = r -> r.readFixed3ByteLong();
        LLARR[4] = r -> r.readFixed4ByteLong();
        LLARR[5] = r -> r.readFixed5ByteLong();
        LLARR[6] = r -> r.readFixed6ByteLong();
        LLARR[7] = r -> r.readFixed7ByteLong();
        LLARR[8] = r -> r.readFixed8ByteLong();
    };

    private int readAsciiInt(final int digits, final FieldDefinition fd) {
        boolean negative = false;
        int result = 0;
        for (int i = 0; i < digits; ++i) {
            final int c = inputdata[parseIndex++];
            if (c == '-') {
                negative = true;
            } else if (c >= '0' && c <= '9') {
                result = 10 * result + (c - '0');
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fd.getName(), getParseIndex(), getClassName());
            }
        }
        return negative ? -result : result;
    }

    private long readAsciiLong(final int digits, final FieldDefinition fd) {
        boolean negative = false;
        long result = 0;
        for (int i = 0; i < digits; ++i) {
            final int c = inputdata[parseIndex++];
            if (c == '-') {
                negative = true;
            } else if (c >= '0' && c <= '9') {
                result = 10 * result + (c - '0');
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fd.getName(), getParseIndex(), getClassName());
            }
        }
        return negative ? -result : result;
    }

    @Override
    public void eatParentSeparator() {
    }
    
    @Override
    public Boolean readBoolean(final MiscElementaryDataItem di) {
        final int x = needToken();
        if (x == 0 || x == '0') {
            return Boolean.FALSE;
        }
        if (x == 1 || x == '1') {
            return Boolean.TRUE;
        }
        throw new MessageParserException(MessageParserException.FIELD_PARSE, "Unexpected value for Boolean in field" + di.getName() + ": " + Integer.toString(x), getParseIndex(), "?");
    }

    @Override
    public boolean readPrimitiveBoolean(final MiscElementaryDataItem di) {
        final int x = needToken();
        if (x == 0 || x == '0') {
            return false;
        }
        if (x == 1 || x == '1') {
            return true;
        }
        throw new MessageParserException(MessageParserException.FIELD_PARSE, "Unexpected value for Boolean in field" + di.getName() + ": " + Integer.toString(x), getParseIndex(), "?");
    }

    @Override
    public ByteArray readByteArray (BinaryElementaryDataItem di) {
        // used for fillers, where we do not need any charset conversion
        final int len = di.getLength();
        require(len);
        final ByteArray ba = new ByteArray(inputdata, parseIndex, len);
        parseIndex += len;
        return ba;
    }

    @Override
    public String readString(final AlphanumericElementaryDataItem di) {
        // create a string of size di..length characters
        final int len = di.getLength();
        require(len);
        final String s = new String(inputdata, parseIndex, len, charset);
        parseIndex += len;
        return s;
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) {
        return readString(di);
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) {
        return readPrimitiveInteger(di);
    }

    private static final Map<FieldDefinition, ToIntBiFunction<MfcobolParser, FieldDefinition>> INT_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        return INT_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), "S9(9) COMP");
            if (pic.fractionalDigits() > 0) {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), null);
            }
            switch (pic.storage()) {
            case BINARY:
                if (pic.integralDigits() == 7 && pic.sign() == PicSignType.UNSIGNED) {
                    // separate from signed: must mask!
                    return (p, fd) -> p.readFixed3ByteInt() & 0xffffff;
                }
                final int numBytes = pic.getSize();
                return (p, fd) -> INTARR[numBytes].applyAsInt(p);
            case DISPLAY:
                return (p, fd) -> readAsciiInt(pic.getSize(), di);
            default:
                ;
            }
            throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), null);
        }).applyAsInt(this, di);
    }

    
    private static final Map<FieldDefinition, BiFunction<MfcobolParser, FieldDefinition, FixedPointBase>> FIXED_POINT_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately

    @Override
    public <F extends FixedPointBase<F>> F readFixedPoint(final BasicNumericElementaryDataItem di, final LongFunction<F> factory) {
        // ugly hacks to avoid generics errors
        final LongFunction fac2 = factory;
        final FixedPointBase base = readFixedPointSub(di, fac2);
        return (F)base;
    }

    public FixedPointBase readFixedPointSub(final BasicNumericElementaryDataItem di, final LongFunction<FixedPointBase> factory) {
        return FIXED_POINT_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), null);
            final int totalDigits = pic.fractionalDigits() + pic.integralDigits();
            if (totalDigits == 7 && pic.sign() == PicSignType.UNSIGNED) {
                // separate from signed: must mask!
                return (p, fd) -> {
                    final int numU = p.readFixed3ByteInt() & 0xffffff;
                    return BigDecimalTools.check(factory.apply(FixedPointBase.mantissaFor(numU, pic.fractionalDigits(), di.getDecimalDigits(), di.getRounding())), di, -1, getClassName());
                };
            }
            final int numBytes = pic.getSize();
            return (p, fd) -> {
                final long mantissa = LLARR[numBytes].applyAsLong(p);
                return BigDecimalTools.check(factory.apply(FixedPointBase.mantissaFor(mantissa, pic.fractionalDigits(), di.getDecimalDigits(), di.getRounding())), di, -1, getClassName());
            };
        }).apply(this, di);
    }

    @Override
    public MessageParserException enumExceptionConverter(final IllegalArgumentException e) {
        return newMPE(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage());
    }

    @Override
    public MessageParserException customExceptionConverter(String msg, Exception e) {
        return newMPE(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg);
    }

    private static final Map<FieldDefinition, BiFunction<MfcobolParser, FieldDefinition, BigDecimal>> BIGDECIMAL_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately

    @Override
    public BigDecimal readBigDecimal(final NumericElementaryDataItem di) throws MessageParserException {
        return BIGDECIMAL_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), null);
            final int totalDigits = pic.fractionalDigits() + pic.integralDigits();
            if (totalDigits == 7 && pic.sign() == PicSignType.UNSIGNED) {
                // separate from signed: must mask!
                return (p, fd) -> {
                    final int numU = p.readFixed3ByteInt() & 0xffffff;
                    return BigDecimal.valueOf(numU, pic.fractionalDigits());
                };
            }
            final int numBytes = pic.getSize();
            return (p, fd) -> {
                return BigDecimal.valueOf(LLARR[numBytes].applyAsLong(p), pic.fractionalDigits());
            };
        }).apply(this, di);
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        final long high = readFixed8ByteLong();
        final long low = readFixed8ByteLong();
        return new UUID(high, low);
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        return readPrimitiveLong(di);
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        final int len = di.getLength();
        if (len == 0) {
            return new byte [0];
        }
        require(len);
        byte [] data = new byte [len];
        System.arraycopy(inputdata, parseIndex, data, 0, len);
        parseIndex += len;
        return data;
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    private static final Map<FieldDefinition, ToLongBiFunction<MfcobolParser, FieldDefinition>> LONG_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        return LONG_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), "S9(18) COMP");
            if (pic.fractionalDigits() > 0) {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), null);
            }
            switch (pic.storage()) {
            case BINARY:
                if (pic.integralDigits() == 7 && pic.sign() == PicSignType.UNSIGNED) {
                    // separate from signed: must mask!
                    return (p, fd) -> p.readFixed3ByteInt() & 0xffffff;
                }
                final int numBytes = pic.getSize();
                return (p, fd) -> LLARR[numBytes].applyAsLong(p);
            case DISPLAY:
                return (p, fd) -> readAsciiLong(pic.getSize(), di);
            default:
                ;
            }
            throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), null);
        }).applyAsLong(this, di);
    }



    private LocalDate intToDay(final int value, final FieldDefinition fd) {
        if (value == 0) {
            if (fd.getIsRequired()) {
                throw new MessageParserException(MessageParserException.EMPTY_BUT_REQUIRED_FIELD, fd.getName(), getParseIndex(), getClassName());
            } else {
                return null;
            }
        }
        if (value < 1970000 || value > 20991231) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        final int year = value / 10000;
        final int month = (value % 10000) / 100;
        final int day = value % 100;
        if (day < 1 || day > 31 || month < 1 || month > 12) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        try {
            return LocalDate.of(year, month, day);
        } catch (final DateTimeException dte) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
    }

    private static final Map<FieldDefinition, BiFunction<MfcobolParser, FieldDefinition, LocalDate>> DAY_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately
    private static final Map<PicNumeric, BiFunction<MfcobolParser, FieldDefinition, LocalDate>> DAY_PARSER_TYPES = new HashMap<>(2);
    static {
        DAY_PARSER_TYPES.put(new PicNumeric(8, 0, false, PicStorageType.BINARY,  PicSignType.UNSIGNED) , (r, fd) -> r.intToDay(r.readFixed4ByteInt(), fd));
        DAY_PARSER_TYPES.put(new PicNumeric(8, 0, false, PicStorageType.DISPLAY, PicSignType.UNSIGNED) , (r, fd) -> r.intToDay(r.readAsciiInt(8, fd), fd));
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        return DAY_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), "9(8) COMP");
            final BiFunction<MfcobolParser, FieldDefinition, LocalDate> parser = DAY_PARSER_TYPES.get(pic);
            if (parser == null) {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), getParseIndex());
            }
            return parser;
        }).apply(this, di);
    }

    private LocalTime intToHHMM(final int value, final FieldDefinition fd) {
        if (value < 0 || value >= 2400) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        final int minutes = (value % 10000) / 100;
        final int seconds = value % 100;
        if (minutes > 59 || seconds > 59) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        try {
            return LocalTime.of(value / 10000, minutes, seconds);
        } catch (final DateTimeException dte) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
    }

    private LocalTime intToHHMMSS(final int value, final FieldDefinition fd) {
        if (value < 0 || value >= 240000) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        final int minutes = value % 100;
        if (minutes > 59) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
        try {
            return LocalTime.of(value / 100, minutes);
        } catch (final DateTimeException dte) {
            throw new MessageParserException(MessageParserException.ILLEGAL_CALENDAR_VALUE, fd.getName(), getParseIndex(), getClassName());
        }
    }

    private static final Map<FieldDefinition, BiFunction<MfcobolParser, FieldDefinition, LocalTime>> TIME_PARSERS = new IdentityHashMap<>(100);  // there is no ConcurrentIdentityHashMap unfortunately
    private static final Map<PicNumeric, BiFunction<MfcobolParser, FieldDefinition, LocalTime>> TIME_PARSER_TYPES = new HashMap<>(4);
    static {
        TIME_PARSER_TYPES.put(new PicNumeric(6, 0, false, PicStorageType.BINARY,  PicSignType.UNSIGNED) , (r, fd) -> r.intToHHMMSS(r.readFixed3ByteInt(), fd));
        TIME_PARSER_TYPES.put(new PicNumeric(4, 0, false, PicStorageType.BINARY,  PicSignType.UNSIGNED) , (r, fd) -> r.intToHHMM  (r.readFixed2ByteInt(), fd));
        TIME_PARSER_TYPES.put(new PicNumeric(6, 0, false, PicStorageType.DISPLAY, PicSignType.UNSIGNED) , (r, fd) -> r.intToHHMMSS(r.readAsciiInt(6, fd), fd));
        TIME_PARSER_TYPES.put(new PicNumeric(4, 0, false, PicStorageType.DISPLAY, PicSignType.UNSIGNED) , (r, fd) -> r.intToHHMM  (r.readAsciiInt(4, fd), fd));
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        return TIME_PARSERS.computeIfAbsent(di, f -> {
            final PicNumeric pic = PicNumeric.forField(di, getClassName(), "9(6) COMP");
            final BiFunction<MfcobolParser, FieldDefinition, LocalTime> parser = TIME_PARSER_TYPES.get(pic);
            if (parser == null) {
                throw new InvalidPictureException(InvalidPictureException.MISSING_PARSER, f.getName(), getClassName(), getParseIndex());
            }
            return parser;
        }).apply(this, di);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public <R extends BonaPortable> R readObject(ObjectReference di, Class<R> type) throws MessageParserException {
        if (di.getLowerBound() == null) {
            throw newMPE(MessageParserException.INVALID_BASE_CLASS_REFERENCE, "");
        }
        final String previousClass = currentClass;
        final String classname = di.getLowerBound().getName();
        final BonaPortable newObject = BonaPortableFactory.createObject(classname);
        if (newObject.getClass() != type) {
            // check if it is a superclass
            throw new MessageParserException(MessageParserException.BAD_CLASS, di.getName(), parseIndex, currentClass); // FIXME: name passed for message
        }
        // parse the new embedded object
        currentClass = classname;
        newObject.deserialize(this);
        currentClass = previousClass;
        return type.cast(newObject);
    }

    @Override
    public Map<String, Object> readJson(ObjectReference di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public List<Object> readArray(ObjectReference di) throws MessageParserException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object readElement(ObjectReference di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws MessageParserException {
        return di.getMaxCount();
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
    }

    @Override
    public BonaPortable readRecord() throws MessageParserException {
        return readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        LOGGER.error("readTransmission() is not supported");
        return Collections.emptyList();
    }

    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }

    @Override
    public Integer readEnum(EnumDataItem edi, BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public String readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
//        final XEnumDefinition spec = di.getBaseXEnum();
////        if (checkForNull(di.getName(), di.getIsRequired() && !spec.getHasNullToken()))
////            return factory.getNullToken();
//        String scannedToken = readString(di.getName());
//        T value = factory.getByToken(scannedToken);
//        if (value == null) {
//            throw newMPE(MessageParserException.INVALID_ENUM_TOKEN, scannedToken);
//        }
//        return value;
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }
}
