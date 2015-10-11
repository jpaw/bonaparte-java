package de.jpaw.bonaparte.core;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
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
import de.jpaw.util.ByteArray;


// improvement idea: in the constructor, a map (or lambda expression) could be passed which says which classes should be serialized as Map and which as object.

// composer which does the same as the standard compact composer, but adds field names
public class CompactJsonComposer extends AbstractCompactComposer {
    
    protected CompactJsonComposer(DataOutput out) {
        super(out, ObjectReuseStrategy.NONE, false);
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        // skip nulls
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
        // skip nulls
    }


    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        super.stringOut(di.getName());
        super.startArray(di, currentMembers, sizeOfElement);
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        super.stringOut(di.getName());
        super.startMap(di, currentMembers);
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
        // not contained in output
    }

//    @Override
//    public void terminateMap() throws IOException {
//        super.terminateMap();
//    }
//
//    @Override
//    public void terminateArray() throws IOException {
//    }
//
//    @Override
//    public void terminateRecord() throws IOException {
//    }
//
//    @Override
//    public void terminateTransmission() throws IOException {
//    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (s != null) {
            super.stringOut(di.getName());
            super.stringOut(s);
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        super.stringOut(di.getName());
        super.addField(di, b);
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        super.stringOut(di.getName());
        super.addField(di, c);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        super.stringOut(di.getName());
        super.addField(di, d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        super.stringOut(di.getName());
        super.addField(di, f);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        super.stringOut(di.getName());
        super.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        super.stringOut(di.getName());
        super.intOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        super.stringOut(di.getName());
        super.intOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        super.stringOut(di.getName());
        super.longOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        if (n != null) {
            super.stringOut(di.getName());
            super.bigintOut(n);
        }
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        if (n != null) {
            super.stringOut(di.getName());
            super.bigdecimalOut(n);
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null) {
            super.stringOut(di.getName());
            super.uuidOut(n);
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b != null) {
            stringOut(di.getName());
            super.bytearrayOut(b);
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b != null) {
            super.stringOut(di.getName());
            super.bytesOut(b);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        if (t != null) {
            super.stringOut(di.getName());
            super.localdateOut(t);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        if (t != null) {
            super.stringOut(di.getName());
            super.localdatetimeOut(t);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        if (t != null) {
            super.stringOut(di.getName());
            super.localtimeOut(t);
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        if (t != null) {
            super.stringOut(di.getName());
            super.longOut(t.getMillis());
        }
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        // nested objects are output as maps as well
        if (obj != null) {
            super.stringOut(di.getName());
            writeObject(obj);
        }
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws IOException {
        // no op
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        // no op
    }

//    @Override
//    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) throws IOException {
//    }
//
//    @Override
//    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws IOException {
//    }
//
//    @Override
//    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
//    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) throws IOException {
        return false;       // perform conversion by default
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) throws IOException {
        if (obj != null) {
            super.stringOut(di.getName());
            super.addField(di, obj);
        }
    }

    @Override
    public void addField(ObjectReference di, Object obj) throws IOException {
        if (obj != null) {
            super.stringOut(di.getName());
            super.addField(di, obj);
        }
    }
    
    @Override
    public void writeRecord(BonaCustom o) throws IOException {
        startRecord();      // Noop by default
        writeObject(o);
        terminateRecord();  // Noop by default
    }
    
    // write an object as map without a field name - nested entry
    @Override
    public void writeObject(BonaCustom o) throws IOException {
        if (o != null) {
            out.write(OBJECT_BEGIN_JSON);
            super.stringOut("$PQON");
            super.stringOut(o.ret$PQON());
            o.serializeSub(this);
            out.write(OBJECT_TERMINATOR);       // terminate object....
        }
    }
    
}
