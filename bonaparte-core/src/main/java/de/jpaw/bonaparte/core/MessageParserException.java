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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.util.ApplicationException;

/**
 * The MessageParserException class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Extends the generic ApplicationException class in order to provide error details which are
 *          specific to parsing of serialized forms (primarily bonaparte data).
 */

public class MessageParserException extends ApplicationException {
	private static final long serialVersionUID = 6578705245543364726L;
	private static final Logger logger = LoggerFactory.getLogger(MessageParserException.class);
	
	private static final int OFFSET = PARSER_ERROR * CLASSIFICATION_FACTOR + 17000; // offset for all codes in this class
	private static boolean textsInitialized = false;
	
	private int characterIndex; // the byte count of the message at which the error occured  
	private String fieldName;	// if known, the name of the field where the error occured
	private String className;	// if known, the name of the class which contained the field

    static public final int MISSING_FIELD_TERMINATOR     = 1;
    static public final int MISSING_RECORD_TERMINATOR    = 2;
    static public final int MISSING_TERMINATOR           = 3;
    static public final int PREMATURE_END                = 4;
    static public final int FIELD_PARSE                  = 5;
    static public final int ILLEGAL_CHAR_ASCII           = 6;
    static public final int ILLEGAL_CHAR_UPPER           = 7;
    static public final int ILLEGAL_CHAR_LOWER           = 8;
    static public final int ILLEGAL_CHAR_DIGIT           = 9;
    static public final int EMPTY_BUT_REQUIRED_FIELD     = 10;
    static public final int UNEXPECTED_CHARACTER         = 11;
    static public final int ILLEGAL_EXPLICIT_NULL        = 12;
    static public final int ILLEGAL_IMPLICIT_NULL        = 13;
    static public final int NO_DIGITS_FOUND              = 14;
    static public final int SUPERFLUOUS_DECIMAL_POINT    = 15;
    static public final int SUPERFLUOUS_EXPONENT         = 16;
    static public final int SUPERFLUOUS_SIGN             = 17;
    static public final int ILLEGAL_CHAR_CTRL            = 18;
    static public final int ILLEGAL_CHAR_NOT_NUMERIC     = 19;
    static public final int NUMERIC_TOO_LONG             = 20;
    static public final int STRING_TOO_LONG              = 21;
    static public final int ILLEGAL_ESCAPE_SEQUENCE      = 22;
    static public final int ILLEGAL_BOOLEAN              = 23;
    static public final int BASE64_PARSING_ERROR         = 24;
    static public final int ILLEGAL_CHAR_BASE64          = 25;
    static public final int ARRAY_SIZE_OUT_OF_BOUNDS     = 26;
    static public final int BAD_CLASS                    = 27;
    static public final int BAD_TRANSMISSION_START       = 28;
    static public final int BAD_TIMESTAMP_FRACTIONALS    = 29;
    static public final int ILLEGAL_DAY                  = 30;
    static public final int ILLEGAL_TIME                 = 31;
    static public final int ILLEGAL_CALENDAR_VALUE       = 32;
	
    /**
     * Method lazyInitialization.
     * 
     * Upload textual descriptions only once they're needed for this type of exception class.
     * The idea is that in working environments, we will never need them ;-).
     * There is a small chance of duplicate initialization, because the access to the flag textsInitialized is not
     * synchronized, but duplicate upload does not hurt (is idempotent)
     */
    static private void lazyInitialization() {
    	synchronized (codeToDescription) {
    		textsInitialized = true;
    		codeToDescription.put(MISSING_FIELD_TERMINATOR     , "Missing field terminator");
    		codeToDescription.put(MISSING_RECORD_TERMINATOR    , "Missing record terminator");
    		codeToDescription.put(MISSING_TERMINATOR           , "Missing message terminator");
    		codeToDescription.put(PREMATURE_END                , "Unexpected end of message");
    		codeToDescription.put(FIELD_PARSE                  , "Field parsing error");
    		codeToDescription.put(ILLEGAL_CHAR_ASCII           , "Field contains non-ASCII character");
    		codeToDescription.put(ILLEGAL_CHAR_UPPER           , "Field must consist of uppercase ASCII only");
    		codeToDescription.put(ILLEGAL_CHAR_LOWER           , "Field must consist of lowercase ASCII only");
    		codeToDescription.put(ILLEGAL_CHAR_DIGIT           , "Field contains non-digit");
    		codeToDescription.put(EMPTY_BUT_REQUIRED_FIELD     , "Field was empty but required non-blank");
    		codeToDescription.put(UNEXPECTED_CHARACTER         , "Character found was not one required next");
    		codeToDescription.put(ILLEGAL_EXPLICIT_NULL        , "NULL not allowed here (required field)");
    		codeToDescription.put(ILLEGAL_IMPLICIT_NULL        , "implicit NULL found due to end of object, not allowed (required field)");
    		codeToDescription.put(NO_DIGITS_FOUND              , "no digits found while parsing a numeric field (possible before exponent)");
    		codeToDescription.put(SUPERFLUOUS_DECIMAL_POINT    , "decimal point found for an integral type, in an exponent, or multiple decimal signs");
    		codeToDescription.put(SUPERFLUOUS_EXPONENT         , "exponent sign encountered for a fixed point field, or multiple exponent signs");
    		codeToDescription.put(SUPERFLUOUS_SIGN             , "minus sign encountered for an unsigned field, or multiple minus signs");
    		codeToDescription.put(ILLEGAL_CHAR_CTRL            , "Field contains control characters");
    		codeToDescription.put(ILLEGAL_CHAR_NOT_NUMERIC     , "Illegal character in numeric field (allowed are only [-.eE0-9]");
    		codeToDescription.put(NUMERIC_TOO_LONG             , "numeric field too long (max 40 characters allowed)");
    		codeToDescription.put(STRING_TOO_LONG              , "String longer than allowed");
    		codeToDescription.put(ILLEGAL_ESCAPE_SEQUENCE      , "Invalid escape sequence (second character must be between @ and _ (0x40..0x5f)");
    		codeToDescription.put(ILLEGAL_BOOLEAN              , "only 0 and 1 are allowed for a boolean field");
    		codeToDescription.put(BASE64_PARSING_ERROR         , "problem parsing a base64 encoded raw field");
    		codeToDescription.put(ILLEGAL_CHAR_BASE64          , "illegal character found while parsing a base64 encoded raw field");
    		codeToDescription.put(ARRAY_SIZE_OUT_OF_BOUNDS     , "negative item count or item count > 1000000000");
    		codeToDescription.put(BAD_CLASS                    , "parsed class is not a subclass of the expected one or subclassing is not allowed");
    		codeToDescription.put(BAD_TRANSMISSION_START       , "Illegal character at the start of a transmission");
    		codeToDescription.put(BAD_TIMESTAMP_FRACTIONALS    , "Illegal number of fractional digits for timestamp (must be 6..9 for precision 0..3)");
    		codeToDescription.put(ILLEGAL_DAY                  , "Illegal day");
    		codeToDescription.put(ILLEGAL_TIME                 , "Illegal time");
    		codeToDescription.put(ILLEGAL_CALENDAR_VALUE       , "Exception converting the date/time");
    	}
    }
	
    private final String getSpecificDescription() {
    	return (className == null ? "?" : className) + "."
			 + (fieldName == null ? "?" : fieldName) + " at position " + characterIndex;
    }
    
    private final void constructorSubroutine(int characterIndex, String className, String fieldName) {
    	this.characterIndex = characterIndex;
    	this.fieldName = fieldName;
    	this.className = className;
    	if (!textsInitialized)
    		lazyInitialization();
    	// for the logger call, do NOT use toString, because that can be overridden, and we're called from a constructor here
    	logger.error("Error " + getErrorCode() + " (" + getStandardDescription() + ") for " + getSpecificDescription());
    }
    
	public MessageParserException(int errorCode, String message, int characterIndex, String className) {
		super(errorCode + OFFSET, message);
		constructorSubroutine(characterIndex, className, null);
	}
	
	public MessageParserException(int errorCode) {
		super(errorCode + OFFSET, null);
		constructorSubroutine(-1, null, null);
	}
	
	@Override
	public String toString() {
		return getSpecificDescription() + ": " + super.toString();
	}
}
