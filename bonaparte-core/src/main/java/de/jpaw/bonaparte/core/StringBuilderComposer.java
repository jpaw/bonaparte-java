 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package de.jpaw.bonaparte.core;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
import javax.xml.bind.DatatypeConverter;

import de.jpaw.util.CharTestsASCII;

/**
 * The StringBuilderComposer class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the marshaller for the bonaparte format using StringBuilder.
 */

public final class StringBuilderComposer extends StringBuilderConstants implements MessageComposer {
	// variables set by constructor
	private StringBuilder work;
    private final boolean writeCRs;
	private final Charset useCharset;

    // restart the output
	@Override
    public void reset() {
        work.setLength(0);
    }

	public StringBuilderComposer(StringBuilder work, boolean writeCRs, Charset useCharset) {
		this.work = work;
		this.writeCRs = writeCRs;
		this.useCharset = useCharset == null ? Charset.forName("UTF-8") : useCharset;;
	}
	
	@Override
    public int getLength() {	// obtain the number of written bytes (composer)
    	return work.length();
    }
	@Override
    public byte[] getBuffer() {
    	return getBytes();
    }

	@Override
	public byte[] getBytes() {
		// TODO Auto-generated method stub
		return work.toString().getBytes(useCharset);
	}
	
	/**************************************************************************************************
	 * Marshalling goes here
	 **************************************************************************************************/

	// the following two methods are provided as separate methods instead of
	// code the single command each time,
	// with the intention that they max become extended or redefined and reused
	// for CSV output to files with
	// customised separators.
	// Because this class is defined as final, I hope the JIT will inline them
	// for better performance
	// THIS IS REQUIRED ONLY LOCALLY
	private void terminateField() {
		work.append(FIELD_TERMINATOR);
	}
	@Override
	public void writeNull() {
		work.append(NULL_FIELD);
	}

	@Override
    public void startTransmission() {
		work.append(TRANSMISSION_BEGIN);
		writeNull();    // blank version number
	}
	@Override
    public void terminateTransmission() {
		work.append(TRANSMISSION_TERMINATOR);
		work.append(TRANSMISSION_TERMINATOR2);
	}

	@Override
	public void terminateRecord() {
		if (writeCRs)
			work.append(RECORD_OPT_TERMINATOR);
		work.append(RECORD_TERMINATOR);
	}

	@Override
	public void terminateObject() {
		work.append(OBJECT_TERMINATOR);
	}

	@Override
	public void startObject(String name, String version) {
		work.append(OBJECT_BEGIN);
		addField(name, 0);
		addField(version, 2);
	}

	@Override
	public void writeSuperclassSeparator() {
		work.append(PARENT_SEPARATOR);
	}

	@Override
	public void startRecord() {
		work.append(RECORD_BEGIN);
		writeNull();  // blank version number
	}

	@Override
	public void writeRecord(BonaPortable o) {
		startRecord();
		o.serialise(this);
		terminateRecord();
	}
	
	// field type specific output functions
	@Override
	public void addEscapedString(String s, int length) {
		if (s != null) {
			for (int i = 0; i < s.length(); ++i) {
				char c = s.charAt(i);
				if (c >= 0 && c < ' ' && c != '\t') {
					work.append(ESCAPE_CHAR);
					work.append(c + '@');
				} else {
					work.append(c);
				}
			}
			terminateField();
		} else {
			writeNull();
		}
	}

	// ascii and unicode
	@Override
	public void addField(String s, int length) {
		if (s != null) {
			work.append(s);
			terminateField();
		} else {
			writeNull();
		}
	}

	// decimal
	@Override
	public void addField(BigDecimal n, int length, int decimals,
			boolean isSigned) {
		if (n != null) {
			work.append(n.toPlainString());
		terminateField();
		} else {
			writeNull();
		}
	}

	// integer
	@Override
	public void addField(Integer n) {
		if (n != null) {
			work.append(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}
	
	// int(n)
	@Override
	public void addField(Integer n, int length, boolean isSigned) {
		if (n != null) {
			work.append(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}

	// long
	@Override
	public void addField(Long n) {
		if (n != null) {
			work.append(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}

	// char (unused!)
	public void addField(Character c) {
		if (c != null) {
			work.append(c);
			terminateField();
		} else {
			writeNull();
		}
	}

	// boolean
	@Override
	public void addField(Boolean b) {
		if (b != null) {
			if (b)
				work.append('1');
			else
				work.append('0');
			terminateField();
		} else {
			writeNull();
		}
	}

	// float
	@Override
	public void addField(Float f) {
		if (f != null) {
			work.append(f.toString());
			terminateField();
		} else {
			writeNull();
		}		
	}

	// double
	@Override
	public void addField(Double d) {
		if (d != null) {
			work.append(d.toString());
			terminateField();
		} else {
			writeNull();
		}
	}

	// raw
	@Override
	public void addField(byte[] b, int length) {
		if (b != null) {
			work.append(DatatypeConverter.printBase64Binary(b));
			//work.append(DatatypeConverter.printHexBinary(b));
			terminateField();
		} else {
			writeNull();
		}
	}

	// Ausgabefunktion für die Datentypen DAY und TIMESTAMP
	@Override
	public void addField(GregorianCalendar t, int length) {  // TODO: length is not needed for this one
		if (t != null) {
			StringBuilder tmpBuffer = new StringBuilder(32);
			int tmpValue = 10000 * t.get(Calendar.YEAR) + 100
					* (t.get(Calendar.MONTH) + 1) + t.get(Calendar.DAY_OF_MONTH);
			tmpBuffer.append(String.format("%d", tmpValue));
			if (length >= 0) {
				// not only day, but also time
				tmpValue = 10000 * t.get(Calendar.HOUR_OF_DAY) + 100
						* t.get(Calendar.MINUTE) + t.get(Calendar.SECOND);
				if (tmpValue != 0 || (length > 0 && t.get(Calendar.MILLISECOND) != 0)) {
					tmpBuffer.append(String.format(".%06d", tmpValue));
					if (length > 0) {
						// add milliseconds
						tmpValue = t.get(Calendar.MILLISECOND);
						if (tmpValue != 0)
							tmpBuffer.append(String.format("%03d", tmpValue));
					}
				}
			}
			work.append(tmpBuffer);
			terminateField();
		} else {
			writeNull();
		}
	}

	@Override
	public void startArray(int currentMembers, int maxMembers) {
		work.append(ARRAY_BEGIN);
		addField(currentMembers);
	}

	@Override
	public void terminateArray() {
		work.append(ARRAY_TERMINATOR);
		
	}

	@Override
	public void addField(BonaPortable obj) {
        // start a new object
		startObject(obj.getMediumClassName(), obj.getRevision());
        // do all fields
        obj.serialiseSub(this);
        // terminate the object
        terminateObject();
		// TODO Auto-generated method stub
		
	}
}
