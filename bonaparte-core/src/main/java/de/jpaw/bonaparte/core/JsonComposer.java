package de.jpaw.bonaparte.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
// import org.joda.time.format.ISODateTimeFormat;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.Multiplicity;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;
import de.jpaw.util.JsonEscaper;

/** This class natively generates JSON output. It aims for compatibility with the extensions used by the json-io library (@Type class information).
 * 
 * @author cobol
 *
 */
public class JsonComposer implements MessageComposer<IOException> {
	protected static final DateTimeFormatter LOCAL_DATE_ISO = DateTimeFormat.forPattern("yyyy-MM-dd"); // ISODateTimeFormat.basicDate();
	protected static final DateTimeFormatter LOCAL_DATETIME_ISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"); // ISODateTimeFormat.basicDateTime();
	protected final Appendable out;
	protected final boolean writeNulls;
	
	protected boolean needFieldSeparator = false;
	
	public JsonComposer(Appendable out, boolean writeNulls) {
		this.out = out;
		this.writeNulls = writeNulls;
	}

	/** Checks if a field separator (',') must be written, and does so if required. Sets the separator to required for the next field. */
	protected void writeSeparator() throws IOException {
		if (needFieldSeparator)
			out.append(',');
		else
			needFieldSeparator = true;
	}

	/** Writes a quoted string. We know that we don't need escaping. */
	protected void writeStringUnescaped(String s) throws IOException {
		out.append('"');
		out.append(s);
		out.append('"');
	}

	/** Writes a quoted fieldname. We assume that no escaping is required, because all valid identifier characters in Java don't need escaping. */
	protected void writeFieldName(FieldDefinition di) throws IOException {
		writeSeparator();
		writeStringUnescaped(di.getName());
		out.append(':');
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

//	/** Write output for a field. The contents is not null and has been converted to Json format already. 
//	 * @throws IOException */
//	protected void writeStringifiedPrimitive(FieldDefinition di, String encoded) throws IOException {
//		writeOptionalFieldName(di);
//		out.append(encoded);
//	}
	
	protected void writeOptionalString(FieldDefinition di, String s) throws IOException {
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
	
	protected void writeOptionalQuotedString(FieldDefinition di, String s) throws IOException {
		if (di.getMultiplicity() != Multiplicity.SCALAR) {
			// must write a null without a name
			writeSeparator();
			if (s == null)
				out.append("null");
			else
				writeStringUnescaped(s);
		} else if (s != null) {
			writeFieldName(di);
			writeStringUnescaped(s);
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
	}

	@Override
	public void startRecord() throws IOException {
	}

	// called for not-null elements only
	@Override
	public void startObject(ObjectReference di, BonaPortable o) throws IOException {
		out.append('{');
		// create the class canonical name as a special field , to be compatible to json-io
		writeStringUnescaped("@Type");
		out.append(':');
		writeStringUnescaped(o.getClass().getCanonicalName());
		needFieldSeparator = true;
	}

	// called for not-null elements only
	@Override
	public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
		writeFieldName(di);
		out.append('[');
	}

	@Override
	public void startMap(FieldDefinition di, int currentMembers) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeSuperclassSeparator() throws IOException {
	}

	@Override
	public void terminateMap() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminateArray() throws IOException {
		out.append(']');
		needFieldSeparator = true;
	}

	@Override
	public void terminateRecord() throws IOException {
	}

	@Override
	public void terminateTransmission() throws IOException {
	}

	@Override
	public void writeRecord(BonaPortable o) throws IOException {
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
	}

	@Override
	public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
		writeOptionalFieldName(di);
		out.append(b ? "true" : "false");
	}

	@Override
	public void addField(MiscElementaryDataItem di, char c) throws IOException {
		writeOptionalFieldName(di);
		JsonEscaper.outputEscapedString(out, String.valueOf(c));
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
			// must write a null without a name
			writeSeparator();
			if (s == null)
				out.append("null");
			else
				JsonEscaper.outputEscapedString(out, s);
		} else if (s != null) {
			writeFieldName(di);
			JsonEscaper.outputEscapedString(out, s);
		} else if (writeNulls) {
			writeFieldName(di);
			out.append("null");
		} // else don't write at all
	}

	@Override
	public void addField(ObjectReference di, BonaPortable obj) throws IOException {
		if (di.getMultiplicity() != Multiplicity.SCALAR) {
			// must write a null without a name
			writeSeparator();
			if (obj == null)
				out.append("null");
			else
				startObject(di, obj);
		} else if (obj != null) {
			writeFieldName(di);
			startObject(di, obj);
		} else if (writeNulls) {
			writeFieldName(di);
			out.append("null");
		} // else don't write at all
	}

	@Override
	public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.toString());
	}

	@Override
	public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addField(BasicNumericElementaryDataItem di, Integer n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.toString());
	}

	@Override
	public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.toString());
	}

	@Override
	public void addField(TemporalElementaryDataItem di, Calendar t) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
		writeOptionalQuotedString(di, t == null ? null : LOCAL_DATE_ISO.print(t));
	}

	@Override
	public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
		writeOptionalQuotedString(di, t == null ? null : LOCAL_DATETIME_ISO.print(t));
	}

	@Override
	public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.toString());
	}

	@Override
	public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.getToken());
	}

	@Override
	public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
		writeOptionalQuotedString(di, n == null ? null : n.getToken());
	}
}
