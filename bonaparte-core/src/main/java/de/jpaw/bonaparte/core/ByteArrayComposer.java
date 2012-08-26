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

import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
/**
 * The ByteArrayComposer class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the serialization for the bonaparte format using byte arrays.
 */

public class ByteArrayComposer extends ByteArrayConstants implements BufferedMessageComposer<RuntimeException> {
	// variables for serialization
	private ByteBuilder work;

	// create a processor for writing
	public ByteArrayComposer() {
		this.work = new ByteBuilder(0, getCharset());
	}

    // restart the output
	@Override
    public void reset() {
        work.setLength(0);
    }
	@Override
    public int getLength() {	// obtain the number of written bytes (composer)
    	return work.length();
    }
	@Override
    public byte[] getBuffer() {
    	return work.getCurrentBuffer();
    }
	@Override
	public byte[] getBytes() {
		return work.getBytes();  // slow!
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
		addField(o);
		terminateRecord();
	}
	
	private void addCharSub(int c) {
		if (c < ' ' && c != '\t') {
			work.append(ESCAPE_CHAR);
			work.append((byte)(c + '@'));
		} else if (c <= 127) {
			// ASCII character: this is faster
			work.append((byte)c);
		} else {
			work.appendUnicode(c);
		}		
	}
	// field type specific output functions
	@Override
	public void addUnicodeString(String s, int length, boolean allowCtrls) {
		if (s != null) {
			for (int i = 0; i < s.length();) {
				int c = s.codePointAt(i);
				addCharSub(c);
				i += Character.charCount(c);
			}
			terminateField();
		} else {
			writeNull();
		}
	}

	// character
	@Override
	public void addField(char c) {
		addCharSub(c);
		terminateField();
	}
	// ascii only (unicode uses different method)
	@Override
	public void addField(String s, int length) {
		if (s != null) {
			work.appendAscii(s);
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
			work.appendAscii(n.toPlainString());
		terminateField();
		} else {
			writeNull();
		}
	}
	
	// byte
	@Override
	public void addField(byte n) {
		work.appendAscii(Byte.toString(n));
		terminateField();
	}
	// short
	@Override
	public void addField(short n) {
		work.appendAscii(Short.toString(n));
		terminateField();
	}
	// integer
	@Override
	public void addField(int n) {
		work.appendAscii(Integer.toString(n));
		terminateField();
	}
	
	// int(n)
	@Override
	public void addField(Integer n, int length, boolean isSigned) {
		if (n != null) {
			work.appendAscii(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}

	// long
	@Override
	public void addField(long n) {
		work.appendAscii(Long.toString(n));
		terminateField();
	}

	// boolean
	@Override
	public void addField(boolean b) {
		if (b)
			work.append((byte) '1');
		else
			work.append((byte) '0');
		terminateField();
	}


	// UUID
	@Override
	public void addField(UUID n) {
		if (n != null) {
			work.appendAscii(n.toString());
			terminateField();
		} else {
			writeNull();
		}
	}
	
	// float
	@Override
	public void addField(float f) {
		work.appendAscii(Float.toString(f));
		terminateField();
	}

	// double
	@Override
	public void addField(double d) {
		work.appendAscii(Double.toString(d));
		terminateField();
	}

	// ByteArray: initial quick & dirty implementation
	@Override
	public void addField(ByteArray b, int length) {
		if (b != null) {
			b.appendBase64(work);
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
			Base64.encodeToByte(work, b, 0, b.length);
			//work.append(DatatypeConverter.printBase64Binary(b));
			//work.append(DatatypeConverter.printHexBinary(b));
			terminateField();
		} else {
			writeNull();
		}
	}

	// append a left padded ASCII String
	private void lpad(String s, int length, byte padCharacter) {
		int l = s.length();
		while (l++ < length)
			work.append(padCharacter);
		work.appendAscii(s);
	}

	// converters for DAY und TIMESTAMP
	@Override
	public void addField(GregorianCalendar t, boolean hhmmss, int length) {  // TODO: length is not needed for this one
		if (t != null) {
			int tmpValue = 10000 * t.get(Calendar.YEAR) + 100
					* (t.get(Calendar.MONTH) + 1) + t.get(Calendar.DAY_OF_MONTH);
			work.appendAscii(Integer.toString(tmpValue));
			if (length >= 0) {
				// not only day, but also time
				if (hhmmss)
					tmpValue = 10000 * t.get(Calendar.HOUR_OF_DAY) + 100
					       * t.get(Calendar.MINUTE) + t.get(Calendar.SECOND);
				else
					tmpValue = 3600 * t.get(Calendar.HOUR_OF_DAY) + 60
				       * t.get(Calendar.MINUTE) + t.get(Calendar.SECOND);
				if (tmpValue != 0 || (length > 0 && t.get(Calendar.MILLISECOND) != 0)) {
					work.append((byte) '.');
					lpad(Integer.toString(tmpValue), 6, (byte) '0');
					if (length > 0) {
						// add milliseconds
						tmpValue = t.get(Calendar.MILLISECOND);
						if (tmpValue != 0)
							lpad(Integer.toString(tmpValue), 3, (byte) '0');
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
			work.appendAscii(Integer.toString(tmpValue));
			terminateField();
		} else {
			writeNull();
		}		
	}

	@Override
	public void addField(LocalDateTime t, boolean hhmmss, int length) {
		if (t != null) {
			int [] values = t.getValues(); // 4 values: year, month, day, millis of day
			//int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
			work.appendAscii(Integer.toString(10000 * values[0] + 100 * values[1] + values[2]));
			if (length >= 0) {
				// not only day, but also time
				//tmpValue = 10000 * t.getHourOfDay() + 100 * t.getMinuteOfHour() + t.getSecondOfMinute();
				if (length > 0 ? (values[3] != 0) : (values[3] / 1000 != 0)) {
					work.append((byte) '.');
					if (hhmmss) {
						int tmpValue = values[3] / 60000; // minutes and hours
						tmpValue = 100 * (tmpValue / 60) + (tmpValue % 60);
						lpad(Integer.toString(tmpValue * 100 + (values[3] % 60000) / 1000), 6, (byte) '0');
					} else {
						lpad(Integer.toString(values[3] / 1000), 6, (byte) '0');
					}
					if (length > 0) {
						// add milliseconds
						int milliSeconds = values[3] % 1000;
						if (milliSeconds != 0)
							lpad(Integer.toString(milliSeconds), 3, (byte) '0');
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
			work.append(OBJECT_BEGIN);
			work.appendAscii(obj.get$PQON());
			terminateField();
			addField(obj.get$Revision(), 0);

			// do all fields (now includes terminator)
			obj.serializeSub(this);
		}
	}

}
