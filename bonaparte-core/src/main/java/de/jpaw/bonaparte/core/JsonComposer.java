package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormat;
import java.time.format.DateTimeFormatter;

import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.IndexType;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.XEnum;
import de.jpaw.json.JsonEscaper;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/** This class natively generates JSON output. It aims for compatibility with the extensions used by the json-io library (@Type class information).
 * See https://github.com/jdereg/json-io and http://code.google.com/p/json-io/ for json-io source and documentation.
 *
 * @author Michael Bischoff (jpaw.de)
 *
 */
public class JsonComposer extends AbstractMessageComposer<IOException> {
    protected static final DateTimeFormatter LOCAL_DATE_ISO = DateTimeFormat.forPattern("yyyy-MM-dd"); // ISODateTimeFormat.basicDate();
    protected static final DateTimeFormatter LOCAL_DATETIME_ISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"); // ISODateTimeFormat.basicDateTime();
    protected static final DateTimeFormatter LOCAL_TIME_ISO = DateTimeFormat.forPattern("HH:mm:ss'Z'"); // ISODateTimeFormat.basicTime();
    protected static final DateTimeFormatter LOCAL_DATETIME_ISO_WITH_MS = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // ISODateTimeFormat.basicDateTime();
    protected static final DateTimeFormatter LOCAL_TIME_ISO_WITH_MS = DateTimeFormat.forPattern("HH:mm:ss.SSS'Z'"); // ISODateTimeFormat.basicTime();
    protected String currentClass = "N/A";
    protected String remFieldName = null;
    protected final Appendable out;
    protected final boolean instantInMillis = false;    // instants are integral seconds, as in JWT iat / exp
    protected final boolean writeNulls;
    protected final boolean writeTypeInfo;      // for every class, also output "@type" and the fully qualified name
    protected final boolean writePqonInfo;      // for every class, also output "@PQON" and the partially qualified name
    protected final boolean maybeWritePqonInfo; // for every class, also output "@PQON" and the partially qualified name, if the containing record allows subclassing
    protected final JsonEscaper jsonEscaper;

    protected enum MapMode {
        NO_MAP, EXPECT_KEY, EXPECT_VALUE
    }
    protected MapMode currentMapMode = MapMode.NO_MAP;

    protected boolean needFieldSeparator = false;
    protected boolean needRecordSeparator = false;

    public static String toJsonString(BonaCustom obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff);
        try {
            bjc.writeRecord(obj);
        } catch (IOException e) {
            // oh yes, sure, StringBuilder throws IOException!
            throw new RuntimeException(e);
        }
        return buff.toString();
    }
    public static String toJsonString(Collection<? extends BonaCustom> obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff);
        try {
            bjc.writeTransmission(obj);
        } catch (IOException e) {
            // oh yes, sure, StringBuilder throws IOException!
            throw new RuntimeException(e);
        }
        return buff.toString();
    }
    public static String toJsonString(Iterable<? extends BonaCustom> obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff);
        try {
            bjc.writeTransmission(obj);
        } catch (IOException e) {
            // oh yes, sure, StringBuilder throws IOException!
            throw new RuntimeException(e);
        }
        return buff.toString();
    }

    @Override
    public void writeObject(BonaCustom o) throws IOException {
        objectOutSub(StaticMeta.OUTER_BONAPORTABLE, o);
    }

    public JsonComposer(Appendable out) {
        this(out, false);
    }
    public JsonComposer(Appendable out, boolean writeNulls) {
        //this(out, writeNulls, false, true, new BonaparteJsonEscaper(out, this)); // this cannot be referenced
        this.out                = out;
        this.writeNulls         = writeNulls;
        this.writeTypeInfo      = false;
        this.writePqonInfo      = false;
        this.maybeWritePqonInfo = true;
        this.jsonEscaper        = new BonaparteJsonEscaper(out, this);
    }
    public JsonComposer(Appendable out, boolean writeNulls, JsonEscaper jsonEscaper) {
        this(out, writeNulls, false, false, true, jsonEscaper);
    }
    public JsonComposer(Appendable out, boolean writeNulls, boolean writeTypeInfo, boolean writePqonInfo, boolean maybeWritePqonInfo, JsonEscaper jsonEscaper) {
        this.out                = out;
        this.writeNulls         = writeNulls;
        this.writeTypeInfo      = writeTypeInfo;
        this.writePqonInfo      = writePqonInfo;
        this.maybeWritePqonInfo = maybeWritePqonInfo;
        this.jsonEscaper        = jsonEscaper;
    }

    /** Checks if a field separator (',') must be written, and does so if required. Sets the separator to required for the next field. */
    protected void writeSeparator() throws IOException {
        if (needFieldSeparator)
            out.append(',');
        else
            needFieldSeparator = true;
    }

    /** Writes a quoted fieldname. We assume that no escaping is required, because all valid identifier characters in Java don't need escaping. */
    protected void writeFieldName(FieldDefinition di) throws IOException {
        if (di.getName().length() > 0) {
            writeSeparator();
            jsonEscaper.outputUnicodeNoControls(di.getName());
            out.append(':');
        }
        // else it's the special JSON outer type to be ignored...
    }

    /** Writes a quoted fieldname, if not in an array, or a separator only. */
    protected void writeOptionalFieldName(FieldDefinition di) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // inside array: must write without a name
            writeSeparator();
        } else {
            writeFieldName(di);
        }
    }

    protected void writeOptionalUnquotedString(FieldDefinition di, String s) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // must write a null without a name
            writeSeparator();
            out.append(s == null ? "null" : s);
        } else if (s != null) {
            writeFieldName(di);
            out.append(s);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        }
    }

    protected void writeOptionalQuotedAscii(FieldDefinition di, String s) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // must write a null without a name
            writeSeparator();
            if (s == null)
                out.append("null");
            else
                jsonEscaper.outputAscii(s);
        } else if (s != null) {
            writeFieldName(di);
            jsonEscaper.outputAscii(s);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        }
    }

    protected void writeOptionalQuotedUnicodeNoControls(FieldDefinition di, String s) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // must write a null without a name
            writeSeparator();
            if (s == null)
                out.append("null");
            else
                jsonEscaper.outputUnicodeNoControls(s);
        } else if (s != null) {
            writeFieldName(di);
            jsonEscaper.outputUnicodeNoControls(s);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        }
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // must write a null without a name
            writeSeparator();
            out.append("null");
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        }
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
        if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        }
    }

    @Override
    public void startTransmission() throws IOException {
        out.append('[');
        needRecordSeparator = false;
    }

    @Override
    public void startRecord() throws IOException {
    }

    // called for not-null elements only
    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws IOException {
        out.append('{');
        needFieldSeparator = false;
        if (writeTypeInfo) {
            // create the class canonical name as a special field, to be compatible to json-io
            jsonEscaper.outputAscii(MimeTypes.JSON_FIELD_FQON);
            out.append(':');
            jsonEscaper.outputUnicodeNoControls(obj.getClass().getCanonicalName());
            needFieldSeparator = true;
        }
        if (writePqonInfo || (maybeWritePqonInfo && di.getAllowSubclasses())) {
            // create the class partially qualified name as a special field, if required
            writeSeparator();
            jsonEscaper.outputAscii(MimeTypes.JSON_FIELD_PQON);
            out.append(':');
            jsonEscaper.outputUnicodeNoControls(obj.ret$PQON());
            needFieldSeparator = true;
        }
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        out.append('}');
        needFieldSeparator = true;
    }


    // called for not-null elements only
    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        writeFieldName(di);
        out.append('[');
        needFieldSeparator = false;
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        // A map does not exist in JSON. The map is however used to model types with variable field names, such as in Swagger 2.0.
        // A map of String type keys is used here.
        // Here, the keys reflect the field names and the value their contents.
        // In order to allow a mixture of fixed names and variable names, and a mixture of variable names with different types,
        // the map does not start a new sub object, but rather is serialized into the current object.
        if (di.getMapIndexType() != IndexType.STRING)
            throw new IOException(new ObjectValidationException(ObjectValidationException.UNSUPPORTED_MAP_KEY_TYPE, di.getName(), currentClass));
        if (/* !(di instanceof ObjectReference) && */ !(di instanceof AlphanumericElementaryDataItem))
            throw new IOException(new ObjectValidationException(ObjectValidationException.UNSUPPORTED_MAP_VALUE_TYPE, di.getName(), currentClass));
        if (currentMapMode != MapMode.NO_MAP)
            throw new IOException(new ObjectValidationException(ObjectValidationException.INVALID_SEQUENCE, di.getName(), currentClass));
        currentMapMode = MapMode.EXPECT_KEY;
    }

    @Override
    public void terminateMap() throws IOException {
        if (currentMapMode != MapMode.EXPECT_KEY)
            throw new IOException(new ObjectValidationException(ObjectValidationException.INVALID_SEQUENCE, "?", currentClass));
        currentMapMode = MapMode.NO_MAP;
    }

    @Override
    public void terminateArray() throws IOException {
        out.append(']');
        needFieldSeparator = true;
    }

    @Override
    public void terminateRecord() throws IOException {
        if (getWriteCRs())
            out.append('\r');
        out.append('\n');
    }

    @Override
    public void terminateTransmission() throws IOException {
        out.append(']');
    }

    // if required, insert a separator character between records.
    @Override
    public void writeRecord(BonaCustom o) throws IOException {
        if (needRecordSeparator)
            out.append(',');
        else
            needRecordSeparator = true;  // next time, I'll need it
        // super.writeRecord(o);  // not working, need a different meta data reference
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE_FOR_JSON, o);
        terminateRecord();
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        writeOptionalFieldName(di);
        out.append(b ? "true" : "false");
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        writeOptionalFieldName(di);
        jsonEscaper.outputUnicodeWithControls(String.valueOf(c));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        writeOptionalFieldName(di);
        out.append(Double.toString(d));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        writeOptionalFieldName(di);
        out.append(Float.toString(f));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        writeOptionalFieldName(di);
        out.append(Byte.toString(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        writeOptionalFieldName(di);
        out.append(Short.toString(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        writeOptionalFieldName(di);
        out.append(Integer.toString(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        writeOptionalFieldName(di);
        out.append(Long.toString(n));
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // in array or map
            switch (currentMapMode) {
            case NO_MAP:        // array
                // must write a null without a name
                writeSeparator();
                if (s == null)
                    out.append("null");
                else
                    jsonEscaper.outputUnicodeWithControls(s);
                break;
            case EXPECT_KEY:
                remFieldName = s;
                currentMapMode = MapMode.EXPECT_VALUE;
                break;
            case EXPECT_VALUE:
                currentMapMode = MapMode.EXPECT_KEY;
                if (s != null || writeNulls) {
                    writeSeparator();
                    jsonEscaper.outputUnicodeNoControls(remFieldName);
                    out.append(':');
                    if (s != null)
                        jsonEscaper.outputUnicodeWithControls(s);
                    else
                        out.append("null");
                }
                break;
            }
        } else if (s != null) {
            writeFieldName(di);
            jsonEscaper.outputUnicodeWithControls(s);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        } // else don't write at all
    }

    protected void objectOutSub(ObjectReference di, BonaCustom obj) throws IOException {
        String previousClass = currentClass;
        currentClass = di.getName();
        startObject(di, obj);
        obj.serializeSub(this);
        terminateObject(di, obj);
        currentClass = previousClass;
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        if (di.getMultiplicity() != Multiplicity.SCALAR) {
            // must write a null without a name
            writeSeparator();
            if (obj == null) {
                out.append("null");
            } else {
                objectOutSub(di, obj);
            }
        } else if (obj != null) {
            writeFieldName(di);
            objectOutSub(di, obj);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        } // else don't write at all
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        writeOptionalQuotedAscii(di, n == null ? null : n.toString());
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b == null) {
            writeNull(di);
        } else {
            ByteBuilder tmp = new ByteBuilder((b.length() * 2) + 4, null);
            Base64.encodeToByte(tmp, b.getBytes(), 0, b.length());
            jsonEscaper.outputAscii(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b == null) {
            writeNull(di);
        } else {
            ByteBuilder tmp = new ByteBuilder((b.length * 2) + 4, null);
            Base64.encodeToByte(tmp, b, 0, b.length);
            jsonEscaper.outputAscii(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
        }
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        writeOptionalUnquotedString(di, n == null ? null : n.toString());
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        writeOptionalUnquotedString(di, n == null ? null : n.toString());
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        writeOptionalQuotedAscii(di, t == null ? null : LOCAL_DATE_ISO.print(t));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        writeOptionalQuotedAscii(di, t == null ? null : di.getFractionalSeconds() > 0 ? LOCAL_DATETIME_ISO_WITH_MS.print(t) : LOCAL_DATETIME_ISO.print(t));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        writeOptionalQuotedAscii(di, t == null ? null : di.getFractionalSeconds() > 0 ? LOCAL_TIME_ISO_WITH_MS.print(t) : LOCAL_TIME_ISO.print(t));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        // must be compatible to ExtendedJsonComposer!
        // writeOptionalQuotedAscii(di, t == null ? null : LOCAL_DATETIME_ISO.print(t));
        if (t == null) {
            writeNull(di);
        } else {
            writeFieldName(di);
            long millis = t.getMillis();
            if (instantInMillis) {
                out.append(Long.toString(millis));
            } else {
                out.append(Long.toString(millis / 1000));
                if (di.getFractionalSeconds() > 0) {
                    millis %= 1000;
                    if (millis > 0)
                        out.append(String.format(".%03d", millis));
                }
            }
        }
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) throws IOException {
        writeOptionalUnquotedString(di, n == null ? null : Integer.toString(n.ordinal()));
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws IOException {
        writeOptionalQuotedUnicodeNoControls(di, n == null ? null : n.getToken());
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
        writeOptionalQuotedUnicodeNoControls(di, n == null ? null : n.getToken());
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) throws IOException {
        return false;       // perform conversion by default
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) throws IOException {
        if (obj == null) {
            writeNull(di);
        } else {
            writeOptionalFieldName(di);
            jsonEscaper.outputJsonObject(obj);
        }
    }
    @Override
    public void addField(ObjectReference di, List<Object> obj) throws IOException {
        if (obj == null) {
            writeNull(di);
        } else {
            writeOptionalFieldName(di);
            jsonEscaper.outputJsonArray(obj);
        }
    }
    @Override
    public void addField(ObjectReference di, Object obj) throws IOException {
        if (obj == null) {
            writeNull(di);
        } else {
            writeOptionalFieldName(di);
            jsonEscaper.outputJsonElement(obj);
        }
    }
}
