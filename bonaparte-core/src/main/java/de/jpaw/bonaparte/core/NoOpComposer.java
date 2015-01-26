package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
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
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** Represents some stub methods. */
public class NoOpComposer<E extends Exception> implements MessageComposer<E> {
    
    public NoOpComposer() {
    }
    
    @Override
    public void writeNull(FieldDefinition di) throws E {
    }
    
    @Override
    public void writeNullCollection(FieldDefinition di) throws E {
    }

    @Override
    public void startTransmission() throws E {
    }

    @Override
    public void startRecord() throws E {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws E {
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws E {
    }

    @Override
    public void writeSuperclassSeparator() throws E {
    }

    @Override
    public void terminateMap() throws E {
    }

    @Override
    public void terminateArray() throws E {
    }

    @Override
    public void terminateRecord() throws E {
    }
    
    @Override
    public void terminateTransmission() throws E {
    }

    @Override
    public void writeRecord(BonaCustom o) throws E {
    }
    
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws E {
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws E {
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws E {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws E {
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws E {
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws E {
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws E {
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws E {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws E {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws E {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws E {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws E {
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws E {
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws E {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws E {
    }


    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) throws E {
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws E {
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws E {
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) throws E {
        return false;       // perform conversion by default
    }
}
