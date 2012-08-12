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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
import javax.xml.bind.DatatypeConverter;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
/**
 * The StringBuilderComposer class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the serialization for the bonaparte format using StringBuilder.
 */

public final class StringBuilderComposer extends StringBuilderConstants implements MessageComposer {
	// variables set by constructor
	private StringBuilder work;

    // restart the output
	@Override
    public void reset() {
        work.setLength(0);
    }

	public StringBuilderComposer(StringBuilder work) {
		this.work = work;
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
		return work.toString().getBytes(getCharset());
	}
	
	/**************************************************************************************************
	 * Serialization goes here
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
		if (doWriteCRs())
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
	
	private void addCharSub(char c) {
		if (c >= 0 && c < ' ' && c != '\t') {
			work.append(ESCAPE_CHAR);
			work.append((char)(c + '@'));
		} else {
			work.append(c);
		}		
	}

	// field type specific output functions
	@Override
	public void addUnicodeString(String s, int length, boolean allowCtrls) {
		if (s != null) {
			for (int i = 0; i < s.length(); ++i) {
				addCharSub(s.charAt(i));
			}
			terminateField();
		} else {
			writeNull();
		}
	}

	// character
	@Override
	public void addField(Character c) {
		if (c != null) {
			addCharSub(c.charValue());
			terminateField();
		} else {
			writeNull();
		}
	}
	// ascii only (unicode uses different method)
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
	
	// byte
	@Override
	public void addField(Byte n) {
		if (n != null) {
			work.append(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}
	// short
	@Override
	public void addField(Short n) {
		if (n != null) {
			work.append(n.toString());
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

	// UUID
	@Override
	public void addField(UUID n) {
		if (n != null) {
			work.append(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}
	
	// ByteArray: initial quick & dirty implementation
	@Override
	public void addField(ByteArray b, int length) {
		if (b != null) {
			work.append(DatatypeConverter.printBase64Binary(b.getBytes()));
			//work.append(DatatypeConverter.printBase64Binary(b));
			//work.append(DatatypeConverter.printHexBinary(b));
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

	// append a left padded String
	private void lpad(String s, int length, char padCharacter) {
		int l = s.length();
		while (l++ < length)
			work.append(padCharacter);
		work.append(s);
	}
	
	// converters for DAY und TIMESTAMP
	@Override
	public void addField(GregorianCalendar t, int length) {  // TODO: length is not needed for this one
		if (t != null) {
			int tmpValue = 10000 * t.get(Calendar.YEAR) + 100
					* (t.get(Calendar.MONTH) + 1) + t.get(Calendar.DAY_OF_MONTH);
			work.append(Integer.toString(tmpValue));
			if (length >= 0) {
				// not only day, but also time
				tmpValue = 10000 * t.get(Calendar.HOUR_OF_DAY) + 100
						* t.get(Calendar.MINUTE) + t.get(Calendar.SECOND);
				if (tmpValue != 0 || (length > 0 && t.get(Calendar.MILLISECOND) != 0)) {
					work.append('.');
					lpad(Integer.toString(tmpValue), 6, '0');
					if (length > 0) {
						// add milliseconds
						tmpValue = t.get(Calendar.MILLISECOND);
						if (tmpValue != 0)
							lpad(Integer.toString(tmpValue), 3, '0');
					}
				}
			}
			terminateField();
		} else {
			writeNull();
		}
	}
	@Override
	public void addField(LocalDate t) {
		if (t != null) {
			int [] values = t.getValues();   // 3 values: year, month, day
			int tmpValue = 10000 * values[0] + 100 * values[1] + values[2];
			// int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
			work.append(Integer.toString(tmpValue));
			terminateField();
		} else {
			writeNull();
		}		
	}

	@Override
	public void addField(LocalDateTime t, int length) {
		if (t != null) {
			int [] values = t.getValues(); // 4 values: year, month, day, millis of day
			int tmpValue = 10000 * values[0] + 100 * values[1] + values[2];
			//int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
			work.append(Integer.toString(tmpValue));
			if (length >= 0) {
				// not only day, but also time
				//tmpValue = 10000 * t.getHourOfDay() + 100 * t.getMinuteOfHour() + t.getSecondOfMinute();
				if (length > 0 ? (values[3] != 0) : (values[3] / 1000 != 0)) {
					work.append('.');
					int milliSeconds = values[3] % 60000; // seconds and millis
					tmpValue = values[3] / 60000; // minutes and hours
					tmpValue = 100 * (tmpValue / 60) + (tmpValue % 60);
					lpad(Integer.toString(tmpValue * 100 + milliSeconds / 1000), 6, '0');
					if (length > 0) {
						// add milliseconds
						milliSeconds = milliSeconds % 1000;
						if (milliSeconds != 0)
							lpad(Integer.toString(milliSeconds), 3, '0');
					}
				}
			}
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
		if (obj == null) {
			writeNull();
		} else {
			// start a new object
			startObject(obj.get$PQON(), obj.get$Revision());
			// do all fields
			obj.serialiseSub(this);
			// terminate the object
			terminateObject();
		}
	}
}
