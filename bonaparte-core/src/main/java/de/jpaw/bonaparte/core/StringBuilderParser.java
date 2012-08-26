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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
import javax.xml.bind.DatatypeConverter;

import de.jpaw.util.ByteArray;
import de.jpaw.util.CharTestsASCII;
import de.jpaw.util.EnumException;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
/**
 * The StringBuilderParser class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the deserialization for the bonaparte format using StringBuilder.
 */

public final class StringBuilderParser extends StringBuilderConstants implements MessageParser<MessageParserException> {
	private final StringBuilder work;
    private int parseIndex;  // for parser
    private int messageLength;  // for parser
    private String currentClass;


	public StringBuilderParser(StringBuilder work, int offset, int length) {
		this.work = work;
	    parseIndex = offset;  // for parser
		messageLength = length < 0 ? work.length() : length; // -1 means full array size
		currentClass = "N/A";
	}
	
	/**************************************************************************************************
	 * Deserialization goes here
	 **************************************************************************************************/
	
	private char needChar() throws MessageParserException {
        if (parseIndex >= messageLength)
        	throw new MessageParserException(MessageParserException.PREMATURE_END, null, parseIndex, currentClass);
        return work.charAt(parseIndex++);
	}
	
	private void needChar(char c) throws MessageParserException {
        if (parseIndex >= messageLength)
        	throw new MessageParserException(MessageParserException.PREMATURE_END,
        			String.format("(expected 0x%02x)", (int)c), parseIndex, currentClass);
        char d = work.charAt(parseIndex++);
        if (c != d)
        	throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
        			String.format("(expected 0x%02x, got 0x%02x)", (int)c, (int)d), parseIndex, currentClass);
	}
	
	// check for Null called for field members inside a class
	private boolean checkForNull(boolean allowNull) throws MessageParserException {
		char c = needChar();
		if (c == NULL_FIELD) {
			if (allowNull)
				return true;
			else
	        	throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, null, parseIndex, currentClass);
		}
		if (c == PARENT_SEPARATOR || c == ARRAY_TERMINATOR) {
			if (allowNull) {
				// uneat it
				--parseIndex;
				return true;
			} else
	        	throw new MessageParserException(MessageParserException.ILLEGAL_IMPLICIT_NULL, null, parseIndex, currentClass);
		}
		--parseIndex;
		return false;
	}
	
	private void skipLeadingSpaces() {
		while (parseIndex < messageLength) {
			char c = work.charAt(parseIndex);
			if (c != ' ' && c != '\t')
				break;
			// skip leading blanks
			++parseIndex;
		}		
	}
	
	private void skipNulls() {
		while (parseIndex < messageLength) {
			char c = work.charAt(parseIndex);
			if (c != NULL_FIELD)
				break;
			// skip trailing NULL objects
			++parseIndex;
		}		
	}

    private String nextIndexParseAscii(boolean allowSign, boolean allowDecimalPoint, boolean allowExponent) throws MessageParserException {
    	final int BUFFER_SIZE = 40;
    	boolean allowSignNextIteration = false;
    	boolean gotAnyDigit = false;
    	StringBuffer tmp = new StringBuffer(BUFFER_SIZE);
    	// skipBlanks: does not hurt!
    	skipLeadingSpaces();
    	if (parseIndex < messageLength && work.charAt(parseIndex) == '+') {
    		// allow positive sign in any case (but not followed by a minus)
    		++parseIndex;
    		allowSign = false;
    	}
        while (parseIndex < messageLength) {
        	char c = work.charAt(parseIndex);
        	if (c == FIELD_TERMINATOR) {
        		if (!gotAnyDigit)
    	        	throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, null, parseIndex, currentClass);
        		++parseIndex;  // eat it!
        		return tmp.toString();
        	}
        	
            if (c == '-') {
            	if (!allowSign)
    	        	throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, null, parseIndex, currentClass);
            } else if (c == '.') {
        		if (!allowDecimalPoint)
    	        	throw new MessageParserException(MessageParserException.SUPERFLUOUS_DECIMAL_POINT, null, parseIndex, currentClass);
        		allowDecimalPoint = false;  // no 2 in a row allowed
        	} else if (c == 'e' || c == 'E') {
        		if (!allowExponent)
    	        	throw new MessageParserException(MessageParserException.SUPERFLUOUS_EXPONENT, null, parseIndex, currentClass);
        		if (!gotAnyDigit)
    	        	throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, null, parseIndex, currentClass);
        		allowSignNextIteration = true;
        		allowExponent = false;
        		allowDecimalPoint = false;
        	} else if (CharTestsASCII.isAsciiDigit(c)) {
        		gotAnyDigit = true;
        	} else {
               	throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, null, parseIndex, currentClass);
        	}
            if (tmp.length() >= BUFFER_SIZE)
            	throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG, null, parseIndex, currentClass);
            tmp.append(c);
            ++parseIndex;
            allowSign = allowSignNextIteration;
    		allowSignNextIteration = false;
        }
        // end of message without appropriate terminator character
    	throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, "(numeric field)", parseIndex, currentClass);
    }
    
	@Override
	public BigDecimal readBigDecimal(boolean allowNull, int length, int decimals, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return new BigDecimal(nextIndexParseAscii(isSigned, true, false));
	}
	
	@Override
	public Character readCharacter(boolean allowNull) throws MessageParserException {
		String tmp = readString(allowNull, 1, false, false, true, true);
		if (tmp == null)
			return null;
		if (tmp.length() == 0)
	    	throw new MessageParserException(MessageParserException.EMPTY_CHAR, null, parseIndex, currentClass);
		return tmp.charAt(0);
	}

	@Override
	public String readAscii(boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws MessageParserException {
		return readString(allowNull, length, doTrim, doTruncate, false, false);
	}
	// readString does the job for Unicode as well as ASCII
	@Override
	public String readString(boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        // OK, read it
        StringBuffer tmp = new StringBuffer(length == 0 ? 32 : length);
        char c;
        if (doTrim) {
        	// skip leading spaces
        	skipLeadingSpaces();
        }
        while ((c = needChar()) != FIELD_TERMINATOR) {
        	if (allowUnicode) {
        		// checks for Unicode characters
        		if (c < ' ') {
        			if (allowCtrls && c == '\t') {
        				; // special case: unescaped TAB character allowed
        			} else if (allowCtrls && c == ESCAPE_CHAR) {
        				c = needChar();
        				if (c < 0x40 || c >= 0x60)
                        	throw new MessageParserException(MessageParserException.ILLEGAL_ESCAPE_SEQUENCE,
                        			String.format("(found 0x%02x)", (int)c), parseIndex, currentClass);
        				c -= 0x40;
        			} else {
                    	throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_CTRL, null, parseIndex, currentClass);
        			}
        		}
        	} else {
        		if (!CharTestsASCII.isAsciiPrintable(c))
                	throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_ASCII,
                			String.format("(found 0x%02x)", (int)c), parseIndex, currentClass);
        	}
        	tmp.append(c);
        }
        if (doTrim) {
        	int l = tmp.length();
        	// trim trailing blanks
        	while (l > 0) {
        		char d = tmp.charAt(l-1);
        		if (d != ' ' && d != '\t')
        			break;  // l is correct length
        		--l;
        	}
        	if (l < tmp.length())
        		tmp.setLength(l);
        }
    	if (length > 0) {
    		// have limits on max size
    		if (tmp.length() > length) {
    			if (doTruncate)
    				tmp.setLength(length);
    			else
    				throw new MessageParserException(MessageParserException.STRING_TOO_LONG,
            			String.format("(exceeds length %d, got so far %s)", length, tmp.toString()),
            			parseIndex, currentClass);
    		}
    	}
		return tmp.toString();
	}

	@Override
	public Boolean readBoolean(boolean allowNull) throws MessageParserException {
        boolean result;
		if (checkForNull(allowNull))
        	return null;
        char c = needChar();
        if (c == '0')
        	result = false;
        else if (c == '1')
            result = true;
        else
        	throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
        			String.format("(found 0x%02x)", (int)c), parseIndex, currentClass);
        needChar(FIELD_TERMINATOR);
        return result;
	}

	public ByteArray readByteArray(boolean allowNull, int length) throws MessageParserException {
		byte [] tmp = readRaw(allowNull, length);
		if (tmp == null)
			return null;
		return new ByteArray(tmp); // TODO: this call does an unnecessary copy
	}
	
	@Override
	public byte[] readRaw(boolean allowNull, int length) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
		int i = parseIndex;
		// find next occurence of field terminator
        while (i < messageLength && work.charAt(i) != FIELD_TERMINATOR)
        	++i;
        if (i == messageLength)
        	throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, "(raw field)", parseIndex, currentClass);
        String tmp = work.substring(parseIndex, i);
        parseIndex = i+1;
        try {
        	return DatatypeConverter.parseBase64Binary(tmp);
        } catch (IllegalArgumentException e) {
        	throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, null, parseIndex, currentClass);
        }
        // return DatatypeConverter.parseHexBinary(tmp);
	}

	@Override
	public GregorianCalendar readGregorianCalendar(boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
		String tmp = nextIndexParseAscii(false, fractionalDigits >= 0, false);  // parse an unsigned numeric string without exponent
		int date;
		int fractional = 0;
		if (fractionalDigits < 0) {
			// day only and we know there is no decimal point
			date = Integer.parseInt(tmp);
			fractional = 0;
		} else {
			int dpoint;
			if ((dpoint = tmp.indexOf('.')) < 0) {
				// day only despite allowed time
				date = Integer.parseInt(tmp);
			} else {
				// day and time
				date = Integer.parseInt(tmp.substring(0, dpoint));
				fractional = Integer.parseInt(tmp.substring(dpoint+1));
				switch (tmp.length() - dpoint - 1) {  // i.e. number of fractional digits
				case 6:  fractional *= 1000; break;   // precisely seconds resolution (timestamp(0))
				case 7:  fractional *= 100; break;
				case 8:  fractional *= 10; break;
				case 9:  break;  // maximum resolution (milliseconds)
				default:  // something weird
	            	throw new MessageParserException(MessageParserException.BAD_TIMESTAMP_FRACTIONALS,
	            			String.format("(found %d)", tmp.length() - dpoint - 1), parseIndex, currentClass);
				}
			}
		}
		// set the date and time
		int day, month, year, hour, minute, second;
		year = date / 10000;
		month = (date %= 10000) / 100;
		day = date %= 100;
		if (hhmmss) {
			hour = fractional / 10000000;
			minute = (fractional %= 10000000) / 100000;
			second = (fractional %= 100000) / 1000;
		} else {
			hour = fractional / 3600000;
			minute = (fractional %= 3600000) / 60000;
			second = (fractional %= 60000) / 1000;
		}
		fractional %= 1000;
		// first checks
		if (year < 1601 || year > 2399 || month == 0 || month > 12 || day == 0
				|| day > 31)
			throw new MessageParserException(
					MessageParserException.ILLEGAL_DAY, String.format(
							"(found %d)", date), parseIndex, currentClass);
		if (hour > 23 || minute > 59 || second > 59) // TODO: allow leap seconds? (that would be seconds == 60)
			throw new MessageParserException(
					MessageParserException.ILLEGAL_TIME,
					String.format("(found %d)", hour * 10000 + minute * 100
							+ second), parseIndex, currentClass);
		// now set the return value
		GregorianCalendar result;
		try {
			// TODO! default is lenient mode, therefore will not check. Solution
			// is to read the data again and compare the values of day, month
			// and year
			result = new GregorianCalendar(year, month - 1, day, hour, minute,
					second);
		} catch (Exception e) {
			throw new MessageParserException(
					MessageParserException.ILLEGAL_CALENDAR_VALUE, null,
					parseIndex, currentClass);
		}
		result.set(Calendar.MILLISECOND, fractional);
		return result;
	}
	@Override
	public LocalDateTime readDayTime(boolean allowNull, boolean hhmmss, int fractionalDigits) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
		String tmp = nextIndexParseAscii(false, fractionalDigits >= 0, false);  // parse an unsigned numeric string without exponent
		int date;
		int fractional = 0;
		int dpoint;
		if ((dpoint = tmp.indexOf('.')) < 0) {
			// day only despite allowed time
			date = Integer.parseInt(tmp);
		} else {
			// day and time
			date = Integer.parseInt(tmp.substring(0, dpoint));
			fractional = Integer.parseInt(tmp.substring(dpoint + 1));
			switch (tmp.length() - dpoint - 1) { // i.e. number of fractional digits
			case 6:
				fractional *= 1000;
				break; // precisely seconds resolution (timestamp(0))
			case 7:
				fractional *= 100;
				break;
			case 8:
				fractional *= 10;
				break;
			case 9:
				break; // maximum resolution (milliseconds)
			default: // something weird
				throw new MessageParserException(
						MessageParserException.BAD_TIMESTAMP_FRACTIONALS,
						String.format("(found %d)", tmp.length() - dpoint - 1),
						parseIndex, currentClass);
			}
		}
		// set the date and time
		int day, month, year, hour, minute, second;
		year = date / 10000;
		month = (date %= 10000) / 100;
		day = date %= 100;
		if (hhmmss) {
			hour = fractional / 10000000;
			minute = (fractional %= 10000000) / 100000;
			second = (fractional %= 100000) / 1000;
		} else {
			hour = fractional / 3600000;
			minute = (fractional %= 3600000) / 60000;
			second = (fractional %= 60000) / 1000;
		}
		fractional %= 1000;
		// first checks
		if (year < 1601 || year > 2399 || month == 0 || month > 12 || day == 0
				|| day > 31)
			throw new MessageParserException(
					MessageParserException.ILLEGAL_DAY, String.format(
							"(found %d)", date), parseIndex, currentClass);
		if (hour > 23 || minute > 59 || second > 59) // TODO: allow leap seconds? (that would be seconds == 60)
			throw new MessageParserException(
					MessageParserException.ILLEGAL_TIME,
					String.format("(found %d)", hour * 10000 + minute * 100
							+ second), parseIndex, currentClass);
		// now set the return value
		LocalDateTime result;
		try {
			// TODO! default is lenient mode, therefore will not check. Solution
			// is to read the data again and compare the values of day, month
			// and year
			result = new LocalDateTime(year, month, day, hour, minute, second, fractional);
		} catch (Exception e) {
			throw new MessageParserException(
					MessageParserException.ILLEGAL_CALENDAR_VALUE, null,
					parseIndex, currentClass);
		}
		return result;
	}
	@Override
	public LocalDate readDay(boolean allowNull) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
		String tmp = nextIndexParseAscii(false, false, false);  // parse an unsigned numeric string without exponent
		int date = Integer.parseInt(tmp);
		// set the date and time
		int day, month, year;
		year = date / 10000;
		month = (date %= 10000) / 100;
		day = date %= 100;
		// first checks
		if (year < 1601 || year > 2399 || month == 0 || month > 12 || day == 0
				|| day > 31)
			throw new MessageParserException(
					MessageParserException.ILLEGAL_DAY, String.format(
							"(found %d)", date), parseIndex, currentClass);
		// now set the return value
		LocalDate result;
		try {
			// TODO! default is lenient mode, therefore will not check. Solution
			// is to read the data again and compare the values of day, month
			// and year
			result = new LocalDate(year, month, day);
		} catch (Exception e) {
			throw new MessageParserException(
					MessageParserException.ILLEGAL_CALENDAR_VALUE, null,
					parseIndex, currentClass);
		}
		return result;
	}

	@Override
	public int parseArrayStart(int max, Class<? extends BonaPortable> type,
			int sizeOfChild) throws MessageParserException {
        char c = needChar();
        if (c == NULL_FIELD)
        	return -1;
        if (c != ARRAY_BEGIN)
        	throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
        			String.format("(expected array start, got 0x%02x)", (int)c), parseIndex, currentClass);
        int n = readInteger(false, false);
        if (n < 0 || n > 1000000000)
        	throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
        			String.format("(got %d entries (0x%x))", n, n), parseIndex, currentClass);
        return n;
	}

	@Override
	public void parseArrayEnd() throws MessageParserException {
		needChar(ARRAY_TERMINATOR);
		
	}

	@Override
	public BonaPortable readRecord() throws MessageParserException {
		BonaPortable result;
		needChar(RECORD_BEGIN);
		needChar(NULL_FIELD); // version no
		result = readObject(BonaPortable.class, false, true);
		needChar(RECORD_TERMINATOR);
		return result;
	}
	
	
	@Override
	public Byte readByte(boolean allowNull, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Byte.valueOf(nextIndexParseAscii(isSigned, false, false));
	}

	@Override
	public Short readShort(boolean allowNull, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Short.valueOf(nextIndexParseAscii(isSigned, false, false));
	}

	@Override
	public Long readLong(boolean allowNull, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Long.valueOf(nextIndexParseAscii(isSigned, false, false));
	}

	@Override
	public Integer readInteger(boolean allowNull, boolean isSigned)	throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Integer.valueOf(nextIndexParseAscii(isSigned, false, false));
	}
	
	@Override
	public Float readFloat(boolean allowNull, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Float.valueOf(nextIndexParseAscii(isSigned, true, true));
	}

	@Override
	public Double readDouble(boolean allowNull, boolean isSigned) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        return Double.valueOf(nextIndexParseAscii(isSigned, true, true));
	}

	@Override
	public void eatParentSeparator() throws MessageParserException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        // TODO: also skip them if not blank, but corresponding flag is set
		needChar(PARENT_SEPARATOR);
	}

	@Override
	public Integer readNumber(boolean allowNull, int length, boolean isSigned)
			throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
		String tmp = nextIndexParseAscii(isSigned, false, false);
		if (tmp.length() > (length + ((tmp.charAt(0) == '-' || tmp.charAt(0) == '+') ? 1 : 0)))
        	throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG,
        			String.format("(allowed %d, found %d)", length, tmp.length()), parseIndex, currentClass);
		return Integer.valueOf(tmp);
	}
	
	@Override
	public BonaPortable readObject(Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws MessageParserException {
		if (checkForNull(allowNull))
        	return null;
        needChar(OBJECT_BEGIN);  // version not yet allowed
		String previousClass = currentClass;
        String classname = readString(false, 0, false, false, false, false);
    	// String revision = readAscii(true, 0, false, false);
        needChar(NULL_FIELD);  // version not yet allowed
        BonaPortable newObject = BonaPortableFactory.createObject(classname);
        //System.out.println("Creating new obj " + classname + " gave me " + newObject);
        // check if the object is of expected type
        if (newObject.getClass() != type) {
        	// check if it is a superclass
        	if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass()))
            	throw new MessageParserException(MessageParserException.BAD_CLASS,
            			String.format("(got %s, expected %s, subclassing = %b)",
            					newObject.getClass().getSimpleName(), type.getSimpleName(), allowSubtypes),
            					parseIndex, currentClass);
        }
        // all good here. Parse the contents
       	currentClass = classname;
        newObject.deserialize(this);
       	currentClass = previousClass;
		return newObject;
	}

	@Override
	public List<BonaPortable> readTransmission() throws MessageParserException {
		List<BonaPortable> results = new ArrayList<BonaPortable>();
		char c = needChar();
		if (c == TRANSMISSION_BEGIN) {
			needChar(NULL_FIELD);  // version
			// TODO: parse extensions here
			while ((c = needChar()) != TRANSMISSION_TERMINATOR) {
				// System.out.println("transmission loop: char is " + c);
				--parseIndex; // push back object def
				results.add(readRecord());
			}
			// when here, last char was transmission terminator
			// optionally eat the last one as well?
		} else if (c == RECORD_BEGIN /* || c == EXTENSION_BEGIN */) {
			// allow single record as a special case
			// TODO: parse extensions here
			--parseIndex;
			results.add(readRecord());
		} else {
        	throw new MessageParserException(MessageParserException.BAD_TRANSMISSION_START,
        			String.format("(got 0x%02x)", (int)c), parseIndex, currentClass);
		}
		// expect that the transmission ends here! TODO: exception if not
		return results;
	}

	@Override
	public UUID readUUID(boolean allowNull) throws MessageParserException {
		String tmp = readAscii(allowNull, 36, true, false);
		if (tmp == null)
        	return null;
		try {
			return UUID.fromString(tmp);
		} catch (IllegalArgumentException e) {
        	throw new MessageParserException(MessageParserException.BAD_UUID_FORMAT,
        			tmp, parseIndex, currentClass);
		}
	}

	@Override
	public MessageParserException enumExceptionConverter(EnumException e) {
		return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.toString(), parseIndex, currentClass);
	}
	
	@Override
	public void setClassName(String newClassName) {
		currentClass = newClassName;
	}
}
