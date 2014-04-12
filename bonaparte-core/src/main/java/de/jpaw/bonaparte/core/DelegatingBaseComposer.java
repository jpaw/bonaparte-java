package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

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
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** Delegates output to the delegateComposer. This class is intended as a superclass for Composers which want to modify only certain aspects of a composer.
 * It is not declared abstract just in order to allow testing (as this class represents the idempotency delegator). */ 
public class DelegatingBaseComposer<E extends Exception> implements MessageComposer<E> {
    protected final MessageComposer<E> delegateComposer; 
    
    public DelegatingBaseComposer(MessageComposer<E> delegateComposer) {
        this.delegateComposer = delegateComposer;
    }

    @Override
    public void writeNull(FieldDefinition di) throws E {
        delegateComposer.writeNull(di);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws E {
        delegateComposer.writeNullCollection(di);
    }
    
    @Override
    public void startTransmission() throws E {
        delegateComposer.startTransmission();
    }

    @Override
    public void startRecord() throws E {
        delegateComposer.startRecord();
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) throws E {
        delegateComposer.startArray(currentMembers, maxMembers, sizeOfElement);
    }

    @Override
    public void startMap(int currentMembers, int indexID) throws E {
        delegateComposer.startMap(currentMembers, indexID);
    }

    @Override
    public void writeSuperclassSeparator() throws E {
        delegateComposer.writeSuperclassSeparator();
    }

    @Override
    public void terminateMap() throws E {
        delegateComposer.terminateMap();
    }

    @Override
    public void terminateArray() throws E {
        delegateComposer.terminateArray();
    }

    @Override
    public void terminateRecord() throws E {
        delegateComposer.terminateRecord();
    }

    @Override
    public void terminateTransmission() throws E {
        delegateComposer.terminateTransmission();
    }

    // cannot delegate this, as it would give away control
    @Override
    public void writeRecord(BonaPortable o) throws E {
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws E {
        delegateComposer.addField(di, s);
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws E {
        delegateComposer.addField(di, b);
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws E {
        delegateComposer.addField(di, c);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws E {
        delegateComposer.addField(di, d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws E {
        delegateComposer.addField(di, f);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, Integer n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws E {
        delegateComposer.addField(di, n);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws E {
        delegateComposer.addField(di, b);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws E {
        delegateComposer.addField(di, b);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) throws E {
        delegateComposer.addField(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws E {
        delegateComposer.addField(di, t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws E {
        delegateComposer.addField(di, t);
    }
    
    @Override
    public void startObject(ObjectReference di, BonaPortable obj) throws E {
        delegateComposer.startObject(di, obj);
    }
    
    // cannot delegate this, as it would give away control
    @Override
    public void addField(ObjectReference di, BonaPortable obj) throws E {
        if (obj == null) {
            writeNull(di);
        } else {
            // start a new object
            startObject(di, obj);
            // do all fields (now includes terminator)
            obj.serializeSub(this);
        }
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws E {
        delegateComposer.addEnum(di, ord, n);
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws E {
        delegateComposer.addEnum(di, token, n);
    }

    // xenum with alphanumeric expansion: delegate to Null/String
	@Override
	public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws E {
        delegateComposer.addEnum(di, token, n);
	}

}
