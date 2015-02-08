package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringParserUtil;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.ByteArray;

/** A parser which takes data through a provided functional interface, any implementation which provides a String get(String).
 * Mainly intended for use from Java 8 applications. */
public class StringProviderParser implements MessageParser<MessageParserException> {
    private final StringParserUtil stringParser = new StringParserUtil();  // TODO; in the form with the provided classname, push it down to the Util
    private final String currentClass;
    private final StringGetter requestParameters;
    
    public static interface StringGetter {
        /** Returns the contents of the field named by name, or null if that field has not been provided. */
        String get(String name) throws MessageParserException;
    }

    /** Utility method: populate BonaPortable from provided getter.
     * Example for Xtend, using a val Map<String,String> m:   obj.unmarshal[m.get(it)].
     * Example for Java 8: unmarshal(obj, m::get) */
    public static void unmarshal(BonaPortable obj, StringGetter getter) throws MessageParserException {
        obj.deserialize(new StringProviderParser(getter, obj.get$PQON()));
    }
    
    public StringProviderParser(StringGetter requestParameters) {
        this.requestParameters = requestParameters;
        this.currentClass = "N/A";
    }
    
    public StringProviderParser(StringGetter requestParameters, String classname) {
        this.requestParameters = requestParameters;
        this.currentClass = classname;
    }
    
    private String getParameter(FieldDefinition di) throws MessageParserException {
        String  data = requestParameters.get(di.getName());
        if (data == null && di.getIsRequired())
            throw new MessageParserException(MessageParserException.EMPTY_BUT_REQUIRED_FIELD, di.getName(), -1, currentClass);
        return data;
    }
    
    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readCharacter(di, getParameter(di));
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readUUID(di, getParameter(di));
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readBoolean(di, getParameter(di));
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readDouble(di, getParameter(di));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readFloat(di, getParameter(di));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readLong(di, getParameter(di));
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readInteger(di, getParameter(di));
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readShort(di, getParameter(di));
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readByte(di, getParameter(di));
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readBigInteger(di, getParameter(di));
    }
    
    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readBigDecimal(di, getParameter(di));
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readAscii(di, getParameter(di));
    }

    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readString(di, getParameter(di));
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        return stringParser.readByteArray(di, getParameter(di));
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        return stringParser.readRaw(di, getParameter(di));
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        return stringParser.readInstant(di, getParameter(di));
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        return stringParser.readDay(di, getParameter(di));
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        return stringParser.readTime(di, getParameter(di));
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        return stringParser.readDayTime(di, getParameter(di));
    }

    @Override
    public <R extends BonaPortable> R readObject(ObjectReference di, Class<R> type) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, di.getName(), -1, currentClass);
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, di.getName(), -1, currentClass);
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE, di.getName(), -1, currentClass);
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public BonaPortable readRecord() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        throw new MessageParserException(MessageParserException.UNSUPPORTED_DATA_TYPE);
    }

    @Override
    public void setClassName(String newClassName) {
        // ignored
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
    }

    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
        return stringParser.readXEnum(di, factory, getParameter(di));
    }

    @Override
    public MessageParserException enumExceptionConverter(IllegalArgumentException e) throws MessageParserException {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage(), -1, currentClass);
    }

    @Override
    public MessageParserException customExceptionConverter(String msg, Exception e) throws MessageParserException {
        return new MessageParserException(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg, -1, currentClass);
    }

    @Override
    public boolean readPrimitiveBoolean(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveBoolean(di, getParameter(di));
    }

    @Override
    public char readPrimitiveCharacter(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveCharacter(di, getParameter(di));
    }

    @Override
    public double readPrimitiveDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveDouble(di, getParameter(di));
    }

    @Override
    public float readPrimitiveFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveFloat(di, getParameter(di));
    }

    @Override
    public long readPrimitiveLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveLong(di, getParameter(di));
    }

    @Override
    public int readPrimitiveInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveInteger(di, getParameter(di));
    }

    @Override
    public short readPrimitiveShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveShort(di, getParameter(di));
    }

    @Override
    public byte readPrimitiveByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        return stringParser.readPrimitiveByte(di, getParameter(di));
    }
}
