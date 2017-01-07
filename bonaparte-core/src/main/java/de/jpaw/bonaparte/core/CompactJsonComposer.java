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
    private int arrayDepth = 0;

    protected CompactJsonComposer(DataOutput out) {
        super(out, ObjectReuseStrategy.NONE, false, true);
    }

    protected void resetArrayDepth() {      // arrayDepth counts the level how deep we are in nested arrays / maps, which determines if field name output is required
        arrayDepth = 0;
    }

    protected void optFieldNameOut(FieldDefinition di) throws IOException {
        if (arrayDepth == 0)
            super.stringOut(di.getName());
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        // skip nulls, but only if not in array
        if (arrayDepth != 0)
            super.writeNull(di);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
        // skip nulls, but only if not in array
        if (arrayDepth != 0)
            super.writeNull(di);
    }


    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        optFieldNameOut(di);
        super.startArray(di, currentMembers, sizeOfElement);
        ++arrayDepth;
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        optFieldNameOut(di);
        super.startMap(di, currentMembers);
        ++arrayDepth;
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
        // not contained in output
    }

    @Override
    public void terminateMap() throws IOException {
        super.terminateMap();
        --arrayDepth;
    }

    @Override
    public void terminateArray() throws IOException {
        --arrayDepth;
    }

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
            optFieldNameOut(di);
            super.stringOut(s);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        optFieldNameOut(di);
        super.addField(di, b);
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        optFieldNameOut(di);
        super.addField(di, c);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        optFieldNameOut(di);
        super.addField(di, d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        optFieldNameOut(di);
        super.addField(di, f);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        optFieldNameOut(di);
        super.addField(di, n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        optFieldNameOut(di);
        super.intOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        optFieldNameOut(di);
        super.intOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        optFieldNameOut(di);
        super.longOut(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        if (n != null) {
            optFieldNameOut(di);
            super.bigintOut(n);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        if (n != null) {
            optFieldNameOut(di);
            super.bigdecimalOut(n);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null) {
            optFieldNameOut(di);
            super.uuidOut(n);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b != null) {
            optFieldNameOut(di);
            super.bytearrayOut(b);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b != null) {
            optFieldNameOut(di);
            super.bytesOut(b);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        if (t != null) {
            optFieldNameOut(di);
            super.localdateOut(t);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        if (t != null) {
            optFieldNameOut(di);
            super.localdatetimeOut(t);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        if (t != null) {
            optFieldNameOut(di);
            super.localtimeOut(t);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        if (t != null) {
            optFieldNameOut(di);
            super.longOut(t.getMillis());
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        // nested objects are output as maps as well
        if (obj != null) {
            optFieldNameOut(di);
            writeObject(obj);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws IOException {
        // no op
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        // no op
    }

    //  the enum methods will forward to one of the existing output methods, the decision logic stays the same
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
            optFieldNameOut(di);
            super.addField(di, obj);
        } else if (arrayDepth != 0)
            writeNull();
    }

    @Override
    public void addField(ObjectReference di, Object obj) throws IOException {
        if (obj != null) {
            optFieldNameOut(di);
            super.addField(di, obj);
        } else if (arrayDepth != 0)
            writeNull();
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
            super.stringOut(MimeTypes.JSON_FIELD_PQON);
            super.stringOut(o.ret$PQON());
            // push array nesting
            final int savedArrayDepth = arrayDepth;
            arrayDepth = 0;
            o.serializeSub(this);
            arrayDepth = savedArrayDepth;
            out.write(OBJECT_TERMINATOR);       // terminate object....
        } else if (arrayDepth != 0)
            writeNull();
    }
}
