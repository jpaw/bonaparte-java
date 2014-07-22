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

    private static final int OFFSET = (PARSER_ERROR * CLASSIFICATION_FACTOR) + 17000; // offset for all codes in this class
    private static final int OFFSET3 = (PARAMETER_ERROR * CLASSIFICATION_FACTOR) + 17000; // offset for all codes in this class
    private static boolean textsInitialized = false;

    private final int characterIndex; // the byte count of the message at which the error occured
    private final String fieldName;   // if known, the name of the field where the error occured
    private final String className;   // if known, the name of the class which contained the field

    static public final int MISSING_FIELD_TERMINATOR     = OFFSET + 1;
    static public final int MISSING_RECORD_TERMINATOR    = OFFSET + 2;
    static public final int MISSING_TERMINATOR           = OFFSET + 3;
    static public final int PREMATURE_END                = OFFSET + 4;
    static public final int FIELD_PARSE                  = OFFSET + 5;
    static public final int ILLEGAL_CHAR_ASCII           = OFFSET + 6;
    static public final int ILLEGAL_CHAR_UPPER           = OFFSET + 7;
    static public final int ILLEGAL_CHAR_LOWER           = OFFSET + 8;
    static public final int ILLEGAL_CHAR_DIGIT           = OFFSET + 9;
    static public final int EMPTY_BUT_REQUIRED_FIELD     = OFFSET + 10;
    static public final int UNEXPECTED_CHARACTER         = OFFSET + 11;
    static public final int ILLEGAL_EXPLICIT_NULL        = OFFSET + 12;
    static public final int ILLEGAL_IMPLICIT_NULL        = OFFSET + 13;
    static public final int NO_DIGITS_FOUND              = OFFSET + 14;
    static public final int SUPERFLUOUS_DECIMAL_POINT    = OFFSET + 15;
    static public final int SUPERFLUOUS_EXPONENT         = OFFSET + 16;
    static public final int SUPERFLUOUS_SIGN             = OFFSET + 17;
    static public final int ILLEGAL_CHAR_CTRL            = OFFSET + 18;
    static public final int ILLEGAL_CHAR_NOT_NUMERIC     = OFFSET + 19;
    static public final int NUMERIC_TOO_LONG             = OFFSET + 20;
    static public final int STRING_TOO_LONG              = OFFSET + 21;
    static public final int ILLEGAL_ESCAPE_SEQUENCE      = OFFSET + 22;
    static public final int ILLEGAL_BOOLEAN              = OFFSET + 23;
    static public final int BASE64_PARSING_ERROR         = OFFSET + 24;
    static public final int ILLEGAL_CHAR_BASE64          = OFFSET + 25;
    static public final int ARRAY_SIZE_OUT_OF_BOUNDS     = OFFSET + 26;
    static public final int BAD_CLASS                    = OFFSET + 27;
    static public final int BAD_TRANSMISSION_START       = OFFSET + 28;
    static public final int BAD_TIMESTAMP_FRACTIONALS    = OFFSET + 29;
    static public final int ILLEGAL_DAY                  = OFFSET + 30;
    static public final int ILLEGAL_TIME                 = OFFSET + 31;
    static public final int ILLEGAL_CALENDAR_VALUE       = OFFSET + 32;
    static public final int EMPTY_CHAR                   = OFFSET + 33;
    static public final int BAD_OBJECT_NAME              = OFFSET + 34;
    static public final int BAD_UUID_FORMAT              = OFFSET + 35;
    static public final int INVALID_ENUM_TOKEN           = OFFSET + 36;
    static public final int CLASS_NOT_FOUND              = OFFSET3 + 37;
    static public final int WRONG_MAP_INDEX_TYPE         = OFFSET + 38;
    static public final int NULL_MAP_NOT_ALLOWED_HERE    = OFFSET + 39;
    static public final int NULL_COLLECTION_NOT_ALLOWED  = OFFSET + 40;
    static public final int TOO_MANY_DECIMALS            = OFFSET + 41;
    static public final int INVALID_BACKREFERENCE        = OFFSET + 42;
    static public final int UNSUPPORTED_DATA_TYPE        = OFFSET + 43;
    static public final int EXTRA_FIELDS                 = OFFSET + 44;
    static public final int TOO_MANY_DIGITS              = OFFSET + 45;


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
            codeToDescription.put(EMPTY_CHAR                   , "empty character field");
            codeToDescription.put(BAD_OBJECT_NAME              , "bad object name (must contain a dot, and not as first or last character)");
            codeToDescription.put(BAD_UUID_FORMAT              , "malformed UUID");
            codeToDescription.put(INVALID_ENUM_TOKEN           , "invalid token to instantiate enum");
            codeToDescription.put(CLASS_NOT_FOUND              , "class could not be found or instantiated");
            codeToDescription.put(WRONG_MAP_INDEX_TYPE         , "parsed index type of map mismatches expected one");
            codeToDescription.put(NULL_MAP_NOT_ALLOWED_HERE    , "parsed NULL for a required Map<>");
            codeToDescription.put(NULL_COLLECTION_NOT_ALLOWED  , "parsed NULL for a required List, Set or Array");
            codeToDescription.put(TOO_MANY_DECIMALS            , "number contains more decimal places than allowed");
            codeToDescription.put(INVALID_BACKREFERENCE        , "The serialized message contains an invalid backreference");
            codeToDescription.put(UNSUPPORTED_DATA_TYPE        , "The request field type or operation is not supported for this cpomposer or parser");
            codeToDescription.put(EXTRA_FIELDS                 , "Extra (non-null) fields have been encountered while expecting a class terminator. Most likely your client JAR is not up to date.");
            codeToDescription.put(TOO_MANY_DIGITS              , "Number too big");
        }
    }

    public final String getSpecificDescription() {
        return (className == null ? "?" : className) + "." + (fieldName == null ? "?" : fieldName) + " at position " + characterIndex;
    }

    public MessageParserException(int errorCode, String fieldName, int characterIndex, String className) {
        super(errorCode, fieldName);
        this.characterIndex = characterIndex;
        this.fieldName = fieldName;
        this.className = className;
        if (!textsInitialized) {
            lazyInitialization();
        }
        // for the logger call, do NOT use toString, because that can be overridden, and we're called from a constructor here
        logger.error("Error " + getErrorCode() + " (" + getStandardDescription() + ") for " + getSpecificDescription());
    }

    public MessageParserException(int errorCode) {
        this(errorCode, null, -1, null);
    }

    @Override
    public String toString() {
        return getSpecificDescription() + ": " + super.toString();
    }
}
