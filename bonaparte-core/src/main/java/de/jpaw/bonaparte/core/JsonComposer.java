package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.enums.BonaByteEnumSet;
import de.jpaw.bonaparte.enums.BonaIntEnumSet;
import de.jpaw.bonaparte.enums.BonaLongEnumSet;
import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaShortEnumSet;
import de.jpaw.bonaparte.enums.BonaStringEnumSet;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.AlphanumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDataItem;
import de.jpaw.bonaparte.util.LongTools;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.json.JsonEscaper;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/** This class natively generates JSON output. It aims for compatibility with the extensions used by the json-io library (@Type class information).
 * See https://github.com/jdereg/json-io and http://code.google.com/p/json-io/ for json-io source and documentation.
 *
 * @author Michael Bischoff (jpaw.de)
 *
 * This implementation uses the following logic when writing fields:
 * - for SCALAR Multiplicity, field name and contents are written
 * - for LIST, SET, ARRAY, only values are written
 * - for Map, a special MapMode determines whether the next element output is a key or the data object.
 *
 */
public class JsonComposer extends AbstractMessageComposer<IOException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonComposer.class);
    protected static final DateTimeFormatter LOCAL_DATE_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected static final DateTimeFormatter LOCAL_DATETIME_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    protected static final DateTimeFormatter LOCAL_TIME_ISO = DateTimeFormatter.ofPattern("HH:mm:ss");
    protected static final DateTimeFormatter LOCAL_DATETIME_ISO_WITH_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    protected static final DateTimeFormatter LOCAL_TIME_ISO_WITH_MS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    protected String currentClass = "N/A";
    protected String remFieldName = null;
    protected int indentation = 0;
    protected final Appendable out;
    protected final boolean instantInMillis = false;    // instants are integral seconds, as in JWT iat / exp
    protected boolean writeEnumOrdinals  = true;      // false: write name, true: write ordinal for non tokenizable enums
    protected boolean writeEnumTokens    = true;      // false: write name, true: write token for tokenizable enums / xenums
    protected boolean writeNulls         = false;
    protected boolean writeTypeInfo      = false;      // for every class, also output "@type" and the fully qualified name
    protected boolean writePqonInfo      = false;      // for every class, also output "@PQON" and the partially qualified name
    protected boolean maybeWritePqonInfo = true; // for every class, also output "@PQON" and the partially qualified name, if the containing record allows subclassing
    protected final JsonEscaper jsonEscaper;

    protected boolean currentMapMode = false;

    protected boolean needFieldSeparator = false;
    protected boolean needRecordSeparator = false;

    public static String toJsonStringNoPQON(BonaCustom obj) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff, false, false, false, false, new BonaparteJsonEscaper(buff));
        bjc.setWriteCRs(false);
        try {
            bjc.writeRecord(obj);
        } catch (IOException e) {
            LOGGER.error("Serialization exception: ", e);
            throw new RuntimeException(e);
        }
        return buff.toString();
    }
    public static String toJsonString(BonaCustom obj) {
        return toJsonString(obj, true, true);
    }
    public static String toJsonString(BonaCustom obj, boolean writeEnumOrdinals, boolean writeEnumTokens) {
        if (obj == null)
            return null;
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff);
        bjc.writeEnumOrdinals = writeEnumOrdinals;
        bjc.writeEnumTokens = writeEnumTokens;
        try {
            bjc.writeRecord(obj);
        } catch (IOException e) {
            LOGGER.error("Serialization exception: ", e);
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
            LOGGER.error("Serialization exception: ", e);
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
            LOGGER.error("Serialization exception: ", e);
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
        this.jsonEscaper        = new BonaparteJsonEscaper(out);
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

    protected void colon() throws IOException {
        out.append(':');
        spaceIfPrettyPrint();
    }

    protected void newLine() throws IOException {
        if (getWriteCRs())
            out.append('\r');
        out.append('\n');
    }

    /** Hook to override if pretty-printing is desired. */
    protected void newLineandIndentIfPrettyPrint() throws IOException {
    }

    protected void openBlock() throws IOException {
        out.append('{');
        ++indentation;
        needFieldSeparator = false;
    }
    protected void closeBlock() throws IOException {
        --indentation;
        if (needFieldSeparator)
            newLineandIndentIfPrettyPrint();
        out.append('}');
        needFieldSeparator = true;
    }

    /** Checks if a field separator (',') must be written, and does so if required. Sets the separator to required for the next field. */
    protected void writeSeparator() throws IOException {
        if (needFieldSeparator) {
            out.append(',');
        } else {
            needFieldSeparator = true;
        }
    }

    /** Hook to override if pretty-printing is desired. */
    protected void spaceIfPrettyPrint() throws IOException {
    }

    /** Checks if a field separator (',') must be written, and does so if required. Sets the separator to required for the next field. */
    protected void writeSeparatorSameLine() throws IOException {
        if (needFieldSeparator) {
            out.append(',');
            spaceIfPrettyPrint();
        } else {
            needFieldSeparator = true;
        }
    }

    /** Writes a quoted fieldname. We assume that no escaping is required, because all valid identifier characters in Java don't need escaping. */
    protected void writeFieldName(FieldDefinition di) throws IOException {
        if (remFieldName != null) {
            // from map mode....   Same criteria could be: if MapMode = expect_value
            writeSeparator();
            newLineandIndentIfPrettyPrint();
            jsonEscaper.outputUnicodeNoControls(remFieldName);
            colon();
            remFieldName = null;
            currentMapMode = true;
            return;
        }
        if (di.getName().length() > 0) {
            writeSeparator();
            newLineandIndentIfPrettyPrint();
            jsonEscaper.outputUnicodeNoControls(di.getName());
            colon();
            return;
        }
        // else it's the special JSON outer type to be ignored...
    }

    protected boolean isListType(FieldDefinition di) {
        switch (di.getMultiplicity()) {
        case ARRAY:
        case LIST:
        case SET:
            return true;
        case MAP:
        case SCALAR:
            return false;
        }
        return false;
    }

    /** Writes a quoted fieldname, if not in an array, or a separator only. */
    protected void writeOptionalFieldName(FieldDefinition di) throws IOException {
        if (isListType(di)) {
            // inside array: must write without a name
            writeSeparatorSameLine();
        } else {
            writeFieldName(di);
        }
    }

    protected void writeOptionalUnquotedString(FieldDefinition di, String s) throws IOException {
        if (isListType(di)) {
            // must write a null without a name
            writeSeparatorSameLine();
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
        if (isListType(di)) {
            // must write a null without a name
            writeSeparatorSameLine();
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
        if (isListType(di)) {
            // must write a null without a name
            writeSeparatorSameLine();
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
        if (isListType(di)) {
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
        openBlock();
        if (writeTypeInfo) {
            // create the class canonical name as a special field, to be compatible to json-io
            newLineandIndentIfPrettyPrint();
            jsonEscaper.outputAscii(MimeTypes.JSON_FIELD_FQON);
            colon();
            jsonEscaper.outputUnicodeNoControls(obj.getClass().getCanonicalName());
            needFieldSeparator = true;
        }
        if (writePqonInfo || (maybeWritePqonInfo && di.getAllowSubclasses())) {
            newLineandIndentIfPrettyPrint();  // indent despite!
            // create the class partially qualified name as a special field, if required
            writeSeparator();
            jsonEscaper.outputAscii(MimeTypes.JSON_FIELD_PQON);
            colon();
            jsonEscaper.outputUnicodeNoControls(obj.ret$PQON());
            needFieldSeparator = true;
        }
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        closeBlock();
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
//        if (di.getMapIndexType() != IndexType.STRING)
//            throw new IOException(new ObjectValidationException(ObjectValidationException.UNSUPPORTED_MAP_KEY_TYPE, di.getName(), currentClass));
//        if (/* !(di instanceof ObjectReference) && */ !(di instanceof AlphanumericElementaryDataItem))
//            throw new IOException(new ObjectValidationException(ObjectValidationException.UNSUPPORTED_MAP_VALUE_TYPE, di.getName(), currentClass));
        if (currentMapMode)
            // nested maps are not allowed / possible
            throw new IOException(new ObjectValidationException(ObjectValidationException.INVALID_SEQUENCE, di.getName(), currentClass));
        currentMapMode = true;
        writeFieldName(di);
        openBlock();
    }

    // actually this is not called, instead, terminateArray() is called!
    @Override
    public void terminateMap() throws IOException {
        if (!currentMapMode)
            throw new IOException(new ObjectValidationException(ObjectValidationException.INVALID_SEQUENCE, "?", currentClass));
        currentMapMode = false;
    }

    @Override
    public void terminateArray() throws IOException {
        if (currentMapMode) {
            // inside map!
            closeBlock();
            currentMapMode = false;
        } else {
            // yes, it was an array, list or set!
            out.append(']');
            needFieldSeparator = true;
        }
    }

    @Override
    public void terminateRecord() throws IOException {
        newLine();
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
        if (di == StaticMeta.MAP_INDEX_META_INTEGER) {
            // just remember the field name for later...
            remFieldName = "_" + Integer.toString(n);
            return;
        }
        writeOptionalFieldName(di);
        out.append(Integer.toString(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        if (di == StaticMeta.MAP_INDEX_META_LONG) {
            // just remember the field name for later...
            remFieldName = "_" + Long.toString(n);
            return;
        }
        LongTools.checkLongOverflow(n);
        writeOptionalFieldName(di);
        out.append(Long.toString(n));
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (di == StaticMeta.MAP_INDEX_META_STRING) {
            // just remember the field name for later...
            remFieldName = s;
            return;
        }
        if (isListType(di)) {
            // in array, list or set
            // must write a null without a name!
            writeSeparator();
            if (s == null)
                out.append("null");
            else
                jsonEscaper.outputUnicodeWithControls(s);
        } else if (s != null) {
            writeFieldName(di);
            jsonEscaper.outputUnicodeWithControls(s);
        } else if (writeNulls) {
            writeFieldName(di);
            out.append("null");
        } // else don't write at all
    }

    protected void objectOutSub(ObjectReference di, BonaCustom obj) throws IOException {
        // PUSH operation for composer state
        String  previousRemFieldName = remFieldName;   remFieldName   = null;
        String  previousClass        = currentClass;   currentClass   = di.getName();
        boolean previousMapMode      = currentMapMode; currentMapMode = false;
        startObject(di, obj);
        obj.serializeSub(this);
        terminateObject(di, obj);
        // POP operation for composer state
        currentMapMode = previousMapMode;
        currentClass   = previousClass;
        remFieldName   = previousRemFieldName;
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        if (isListType(di)) {
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
            String s = new String(tmp.getCurrentBuffer(), 0, tmp.length());
            writeOptionalQuotedAscii(di, s);
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b == null) {
            writeNull(di);
        } else {
            ByteBuilder tmp = new ByteBuilder((b.length * 2) + 4, null);
            Base64.encodeToByte(tmp, b, 0, b.length);
            String s = new String(tmp.getCurrentBuffer(), 0, tmp.length());
            writeOptionalQuotedAscii(di, s);
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
        writeOptionalQuotedAscii(di, t == null ? null : t.format(LOCAL_DATE_ISO));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        writeOptionalQuotedAscii(di, t == null ? null : di.getFractionalSeconds() > 0 ? t.format(LOCAL_DATETIME_ISO_WITH_MS) : t.format(LOCAL_DATETIME_ISO));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        writeOptionalQuotedAscii(di, t == null ? null : di.getFractionalSeconds() > 0 ? t.format(LOCAL_TIME_ISO_WITH_MS) : t.format(LOCAL_TIME_ISO));
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        // must be compatible to ExtendedJsonComposer!
        if (t == null) {
            writeNull(di);
        } else {
            writeFieldName(di);
            long millis = t.toEpochMilli();
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
        if (writeEnumOrdinals) {
            writeOptionalUnquotedString(di, n == null ? null : Integer.toString(n.ordinal()));
        } else {
            writeOptionalQuotedUnicodeNoControls(di, n == null ? null : n.name());
        }
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws IOException {
        writeOptionalQuotedUnicodeNoControls(di, n == null ? null : writeEnumTokens ? n.getToken() : n.name());
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
        writeOptionalQuotedUnicodeNoControls(di, n == null ? null : writeEnumTokens ? n.getToken() : n.name());
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

    protected <S extends Enum<S>> void writeEnumset(FieldDefinition di, Set<Enum<S>> s) throws IOException {
        writeOptionalFieldName(di);
        out.append('[');
        boolean needComma = false;
        for (Enum<S> t: s) {
            if (needComma) {
                out.append(',');
            } else {
                needComma = true;  // next time
            }
            jsonEscaper.outputUnicodeNoControls(t.name());
        }
        out.append(']');
    }

    // output a non-null numeric enumset, as list of instance names
    protected void writeAsArray(NumericEnumSetDataItem di, long n) throws IOException {
        writeOptionalFieldName(di);
        out.append('[');
        EnumDefinition edi = di.getBaseEnumset().getBaseEnum();
        // write a list of instance names
        List<String> ids = edi.getIds();
        int ordinal = 0;
        boolean needComma = false;
        while (n != 0L) {
            if ((n & 1L) != 0L ) {
                if (needComma) {
                    out.append(',');
                } else {
                    needComma = true;  // next time
                }
                jsonEscaper.outputUnicodeNoControls(ids.get(ordinal));
            }
            ++ordinal;
            n >>>= 1;
        }
        out.append(']');
    }

    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaByteEnumSet<S> n) throws IOException {
        if (n == null) {
            writeNull(di);
        } else if (writeEnumOrdinals) {
            addField(di, n.getBitmap());
        } else {
            // writeEnumset(di, n);  // Java does not like it. Bug?
            writeAsArray(di, n.getBitmap());
        }
    }

    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaShortEnumSet<S> n) throws IOException {
        if (n == null) {
            writeNull(di);
        } else if (writeEnumOrdinals) {
            addField(di, n.getBitmap());
        } else {
            writeAsArray(di, n.getBitmap());
        }
    }

    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaIntEnumSet<S> n) throws IOException {
        if (n == null) {
            writeNull(di);
        } else if (writeEnumOrdinals) {
            addField(di, n.getBitmap());
        } else {
            writeAsArray(di, n.getBitmap());
        }
    }

    @Override
    public <S extends Enum<S>> void addField(NumericEnumSetDataItem di, BonaLongEnumSet<S> n) throws IOException {
        if (n == null) {
            writeNull(di);
        } else if (writeEnumOrdinals) {
            addField(di, n.getBitmap());
        } else {
            writeAsArray(di, n.getBitmap());
        }
    }

    // output a non-null numeric enumset, as list of instance names
    protected <S extends TokenizableEnum> void writeAlphaEnumSet(AlphanumericElementaryDataItem di, BonaStringEnumSet<S> e) throws IOException {
        if (e == null) {
            writeNull(di);
        } else if (writeEnumTokens) {
            addField(di, e.getBitmap());
        } else {
            writeOptionalFieldName(di);
            out.append('[');
            boolean needComma = false;
            for (TokenizableEnum t: e) {
                if (needComma) {
                    out.append(',');
                } else {
                    needComma = true;  // next time
                }
                jsonEscaper.outputUnicodeNoControls(t.name());
            }
            out.append(']');
        }
    }

    @Override
    public <S extends TokenizableEnum> void addField(AlphanumericEnumSetDataItem di, BonaStringEnumSet<S> e) throws IOException {
        writeAlphaEnumSet(di, e);
    }

    @Override
    public <S extends TokenizableEnum> void addField(XEnumSetDataItem di, BonaStringEnumSet<S> e) throws IOException {
        writeAlphaEnumSet(di, e);
    }

    // Java boilerplate code below:
    public boolean isWriteEnumOrdinals() {
        return writeEnumOrdinals;
    }
    public void setWriteEnumOrdinals(boolean writeEnumOrdinals) {
        this.writeEnumOrdinals = writeEnumOrdinals;
    }
    public boolean isWriteEnumTokens() {
        return writeEnumTokens;
    }
    public void setWriteEnumTokens(boolean writeEnumTokens) {
        this.writeEnumTokens = writeEnumTokens;
    }
    public boolean isWriteNulls() {
        return writeNulls;
    }
    public void setWriteNulls(boolean writeNulls) {
        this.writeNulls = writeNulls;
    }
    public boolean isWriteTypeInfo() {
        return writeTypeInfo;
    }
    public void setWriteTypeInfo(boolean writeTypeInfo) {
        this.writeTypeInfo = writeTypeInfo;
    }
    public boolean isWritePqonInfo() {
        return writePqonInfo;
    }
    public void setWritePqonInfo(boolean writePqonInfo) {
        this.writePqonInfo = writePqonInfo;
    }
    public boolean isMaybeWritePqonInfo() {
        return maybeWritePqonInfo;
    }
    public void setMaybeWritePqonInfo(boolean maybeWritePqonInfo) {
        this.maybeWritePqonInfo = maybeWritePqonInfo;
    }
	@Override
	public <F extends FixedPointBase<F>> void addField(BasicNumericElementaryDataItem di, F n) throws IOException {
        writeOptionalUnquotedString(di, n == null ? null : n.toString());
	}
}
