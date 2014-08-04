package de.jpaw.bonaparte.core;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

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
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID readUUID(MiscElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String readString(AlphanumericElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BonaPortable readObject(ObjectReference di, Class<? extends BonaPortable> type) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws IOException {
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
}
