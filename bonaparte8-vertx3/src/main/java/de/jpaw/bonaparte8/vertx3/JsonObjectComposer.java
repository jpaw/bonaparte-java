package de.jpaw.bonaparte8.vertx3;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.jpaw.bonaparte.core.AbstractMessageComposer;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.core.StaticMeta;
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

/** Composer which serializes all data into a vertx JsonObject */
public class JsonObjectComposer extends AbstractMessageComposer<RuntimeException> {
    protected static final DateTimeFormatter LOCAL_DATE_ISO = ISODateTimeFormat.basicDate();
    protected static final DateTimeFormatter LOCAL_DATETIME_ISO = ISODateTimeFormat.basicDateTime();
    protected static final DateTimeFormatter LOCAL_TIME_ISO = ISODateTimeFormat.basicTime();

    protected JsonObject obj = null;
    protected JsonArray arr = null;
    protected boolean inArray = false;
    protected boolean writeNulls = false;
    protected String rememberedArrayFieldName = null;


    // static converter method for convenience
    public static JsonObject toJsonObject(BonaCustom o, boolean writeNulls) {
        JsonObjectComposer composer = new JsonObjectComposer(writeNulls);
        composer.writeRecord(o);
        return composer.getObject();
    }
    public static JsonObject toJsonObject(BonaCustom o) {
        return toJsonObject(o, false);
    }

    // retrieve the converted object
    public JsonObject getObject() {
        return obj;
    }

    public JsonObjectComposer() {
    }

    public JsonObjectComposer(boolean writeNulls) {
        this.writeNulls = writeNulls;
    }


    @Override
    public void writeNull(FieldDefinition di) { // nulls are not written, unless we are in an array
        if (inArray) {
            arr.addNull();
        } else if (writeNulls) {
            obj.putNull(di.getName());
        }
    }

    @Override
    public void writeNullCollection(FieldDefinition di) {
    }

    @Override
    public void startTransmission() {
    }

    @Override
    public void startRecord() {
        // reset to consistent state for sanity
        obj = null;
        arr = null;
        inArray = false;
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom o) {
        obj = new JsonObject();
        obj.put(MimeTypes.JSON_FIELD_PQON, o.ret$PQON());  // insert the actual object type
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom o) {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) {
        if (inArray)
            throw new IllegalArgumentException("Nested arrays are not supported");
        inArray = true;
        arr = new JsonArray();
        rememberedArrayFieldName = di.getName();
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) {
        throw new IllegalArgumentException("Maps are not supported for Json conversion.");
    }

    @Override
    public void writeSuperclassSeparator() {
    }

    @Override
    public void terminateMap() {
    }

    @Override
    public void terminateArray() {
        if (!inArray) {
            throw new IllegalArgumentException("Cannot terminate array, as non is open");
        }
        obj.put(rememberedArrayFieldName, arr);
        inArray = false;
        arr = null;
    }

    @Override
    public void terminateRecord() {
    }

    @Override
    public void terminateTransmission() {
    }

    @Override
    public void writeRecord(BonaCustom o) {
        startRecord();
        // addField(StaticMeta.OUTER_BONAPORTABLE, o);
        // start a new object
        startObject(StaticMeta.OUTER_BONAPORTABLE, o);
        o.serializeSub(this);
        terminateObject(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
    }

    @Override
    public void addField(ObjectReference di, BonaCustom o) {
        if (o == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(toJsonObject(o, writeNulls));
            else
                obj.put(di.getName(), toJsonObject(o, writeNulls));
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        if (inArray)
            arr.add(b);
        else
            obj.put(di.getName(), b);
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        writeNonNull(di, String.valueOf(c));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        if (inArray)
            arr.add(d);
        else
            obj.put(di.getName(), d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        if (inArray)
            arr.add(f);
        else
            obj.put(di.getName(), f);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        if (inArray)
            arr.add(n);
        else
            obj.put(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        if (inArray)
            arr.add(n);
        else
            obj.put(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        if (inArray)
            arr.add(n);
        else
            obj.put(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        if (inArray)
            arr.add(n);
        else
            obj.put(di.getName(), n);
    }

    protected void writeNonNull(FieldDefinition di, String s) {
        if (inArray)
            arr.add(s);
        else
            obj.put(di.getName(), s);
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        if (s == null) {
            writeNull(di);
        } else {
            writeNonNull(di, s);
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        if (n == null) {
            writeNull(di);
        } else {
            writeNonNull(di, n.toString());
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        if (b == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(b.getBytes());
            else
                obj.put(di.getName(), b.getBytes());
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(b);
            else
                obj.put(di.getName(), b);
        }
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) {
        if (n == null) {
            writeNull(di);
        } else {
            writeNonNull(di, n.toString());
        }
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        if (n == null) {
            writeNull(di);
        } else {
            writeNonNull(di, n.toString());
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        if (t == null) {
            writeNull(di);
        } else {
            writeNonNull(di, LOCAL_DATE_ISO.print(t));
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        if (t == null) {
            writeNull(di);
        } else {
            writeNonNull(di, LOCAL_DATETIME_ISO.print(t));
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
        if (t == null) {
            writeNull(di);
        } else {
            writeNonNull(di, LOCAL_TIME_ISO.print(t));
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
        if (t == null) {
            writeNull(di);
        } else {
            writeNonNull(di, LOCAL_DATETIME_ISO.print(t));
        }
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) {
        if (n == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(n.ordinal());
            else
                obj.put(di.getName(), n.ordinal());
        }
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) {
        if (n == null) {
            writeNull(di);
        } else {
            writeNonNull(di, n.getToken());
        }
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) {
        if (n == null) {
            writeNull(di);
        } else {
            writeNonNull(di, n.getToken());
        }
    }

    @Override
    public boolean addExternal(ObjectReference di, Object o) {
        return false;       // perform conversion by default
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> o) {
        if (o == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(new JsonObject(o));
            else
                obj.put(di.getName(), new JsonObject(o));
        }
    }

    @Override
    public void addField(ObjectReference di, List<Object> o) throws RuntimeException {
        if (o == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(new JsonArray(o));
            else
                obj.put(di.getName(), new JsonArray(o));
        }
    }

    @Override
    public void addField(ObjectReference di, Object o) {
        if (o == null) {
            writeNull(di);
        } else {
            if (inArray)
                arr.add(o);
            else
                obj.put(di.getName(), o);
        }
    }
}
