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
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageParserException.class);

    private static final int OFFSET  = (CL_PARSER_ERROR         * CLASSIFICATION_FACTOR) + 17000;
    private static final int OFFSET3 = (CL_PARAMETER_ERROR      * CLASSIFICATION_FACTOR) + 17000;
    private static final int OFFSET8 = (CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + 17000;

    public static final int MISSING_FIELD_TERMINATOR     = OFFSET + 1;
    public static final int MISSING_RECORD_TERMINATOR    = OFFSET + 2;
    public static final int MISSING_TERMINATOR           = OFFSET + 3;
    public static final int PREMATURE_END                = OFFSET + 4;
    public static final int FIELD_PARSE                  = OFFSET + 5;
    public static final int ILLEGAL_CHAR_ASCII           = OFFSET + 6;
    public static final int ILLEGAL_CHAR_UPPER           = OFFSET + 7;
    public static final int ILLEGAL_CHAR_LOWER           = OFFSET + 8;
    public static final int ILLEGAL_CHAR_DIGIT           = OFFSET + 9;
    public static final int EMPTY_BUT_REQUIRED_FIELD     = OFFSET + 10;
    public static final int UNEXPECTED_CHARACTER         = OFFSET + 11;
    public static final int ILLEGAL_EXPLICIT_NULL        = OFFSET + 12;
    public static final int ILLEGAL_IMPLICIT_NULL        = OFFSET + 13;
    public static final int NO_DIGITS_FOUND              = OFFSET + 14;
    public static final int SUPERFLUOUS_DECIMAL_POINT    = OFFSET + 15;
    public static final int SUPERFLUOUS_EXPONENT         = OFFSET + 16;
    public static final int SUPERFLUOUS_SIGN             = OFFSET + 17;
    public static final int ILLEGAL_CHAR_CTRL            = OFFSET + 18;
    public static final int ILLEGAL_CHAR_NOT_NUMERIC     = OFFSET + 19;
    public static final int NUMERIC_TOO_LONG             = OFFSET + 20;
    public static final int STRING_TOO_LONG              = OFFSET + 21;
    public static final int ILLEGAL_ESCAPE_SEQUENCE      = OFFSET + 22;
    public static final int ILLEGAL_BOOLEAN              = OFFSET + 23;
    public static final int BASE64_PARSING_ERROR         = OFFSET + 24;
    public static final int ILLEGAL_CHAR_BASE64          = OFFSET + 25;
    public static final int ARRAY_SIZE_OUT_OF_BOUNDS     = OFFSET + 26;
    public static final int BAD_CLASS                    = OFFSET + 27;
    public static final int BAD_TRANSMISSION_START       = OFFSET + 28;
    public static final int BAD_TIMESTAMP_FRACTIONALS    = OFFSET + 29;
    public static final int ILLEGAL_DAY                  = OFFSET + 30;
    public static final int ILLEGAL_TIME                 = OFFSET + 31;
    public static final int ILLEGAL_CALENDAR_VALUE       = OFFSET + 32;
    public static final int EMPTY_CHAR                   = OFFSET + 33;
    public static final int BAD_OBJECT_NAME              = OFFSET + 34;
    public static final int BAD_UUID_FORMAT              = OFFSET + 35;
    public static final int INVALID_ENUM_TOKEN           = OFFSET + 36;
    public static final int CLASS_NOT_FOUND              = OFFSET3 + 37;
    public static final int WRONG_MAP_INDEX_TYPE         = OFFSET + 38;
    public static final int NULL_MAP_NOT_ALLOWED_HERE    = OFFSET + 39;
    public static final int NULL_COLLECTION_NOT_ALLOWED  = OFFSET + 40;
    public static final int TOO_MANY_DECIMALS            = OFFSET + 41;
    public static final int INVALID_BACKREFERENCE        = OFFSET + 42;
    public static final int UNSUPPORTED_DATA_TYPE        = OFFSET8 + 43;  // this is a coding problem
    public static final int EXTRA_FIELDS                 = OFFSET + 44;
    public static final int TOO_MANY_DIGITS              = OFFSET + 45;
    public static final int UNKNOW_RECORD_TYPE           = OFFSET + 46;
    public static final int NULL_CLASS_PQON              = OFFSET + 47;
    public static final int INVALID_BASE_CLASS_REFERENCE = OFFSET + 48;
    public static final int CUSTOM_OBJECT_EXCEPTION      = OFFSET + 49;
    public static final int NUMERIC_TOO_MANY_DIGITS      = OFFSET + 50;
    public static final int CHAR_TOO_LONG                = OFFSET + 51;
    public static final int NUMBER_PARSING_ERROR         = OFFSET + 52;
    public static final int BAD_CLASS_IDS                = OFFSET + 53;
    public static final int INVALID_REFERENCES           = OFFSET + 54;
    public static final int UNSUPPORTED_TOKEN            = OFFSET + 55;
    public static final int UNSUPPORTED_COMPRESSED       = OFFSET + 56;
    public static final int JSON_EXCEPTION               = OFFSET + 57;
    public static final int JSON_ID                      = OFFSET + 58;
    public static final int JSON_DUPLICATE_KEY           = OFFSET + 59;
    public static final int JSON_NO_PQON                 = OFFSET + 60;
    public static final int INVALID_CHAR                 = OFFSET + 61;
    public static final int UNSUPPORTED_CONVERSION       = OFFSET + 62;
    public static final int BINARY_TOO_LONG              = OFFSET + 63;
    public static final int JSON_BAD_OBJECTREF           = OFFSET8 + 64;
    public static final int WRONG_CLASS                  = OFFSET + 65;
    public static final int JSON_EXCEPTION_MAP           = OFFSET + 66;
    public static final int JSON_EXCEPTION_ARRAY         = OFFSET + 67;
    public static final int JSON_EXCEPTION_OBJECT        = OFFSET + 68;
    public static final int INVALID_ENUM_NAME            = OFFSET + 69;
    public static final int STRING_TOO_SHORT             = OFFSET + 70;
    public static final int MISSING_CLOSING_QUOTE        = OFFSET + 71;


    static {
        registerRange(OFFSET, false, MessageParserException.class, ApplicationLevelType.CORE_LIBRARY);

        registerCode(MISSING_FIELD_TERMINATOR     , "Missing field terminator");
        registerCode(MISSING_RECORD_TERMINATOR    , "Missing record terminator");
        registerCode(MISSING_TERMINATOR           , "Missing message terminator");
        registerCode(PREMATURE_END                , "Unexpected end of message");
        registerCode(FIELD_PARSE                  , "Field parsing error");
        registerCode(ILLEGAL_CHAR_ASCII           , "Field contains non-ASCII character");
        registerCode(ILLEGAL_CHAR_UPPER           , "Field must consist of uppercase ASCII only");
        registerCode(ILLEGAL_CHAR_LOWER           , "Field must consist of lowercase ASCII only");
        registerCode(ILLEGAL_CHAR_DIGIT           , "Field contains non-digit");
        registerCode(EMPTY_BUT_REQUIRED_FIELD     , "Field was empty but required non-blank");
        registerCode(UNEXPECTED_CHARACTER         , "Character found was not one required next");
        registerCode(ILLEGAL_EXPLICIT_NULL        , "NULL not allowed here (required field)");
        registerCode(ILLEGAL_IMPLICIT_NULL        , "implicit NULL found due to end of object, not allowed (required field)");
        registerCode(NO_DIGITS_FOUND              , "no digits found while parsing a numeric field (possible before exponent)");
        registerCode(SUPERFLUOUS_DECIMAL_POINT    , "decimal point found for an integral type, in an exponent, or multiple decimal signs");
        registerCode(SUPERFLUOUS_EXPONENT         , "exponent sign encountered for a fixed point field, or multiple exponent signs");
        registerCode(SUPERFLUOUS_SIGN             , "minus sign encountered for an unsigned field, or multiple minus signs");
        registerCode(ILLEGAL_CHAR_CTRL            , "Field contains control characters");
        registerCode(ILLEGAL_CHAR_NOT_NUMERIC     , "Illegal character in numeric field (allowed are only [-.eE0-9]");
        registerCode(NUMERIC_TOO_LONG             , "numeric field too long (max 40 characters allowed)");
        registerCode(STRING_TOO_LONG              , "String longer than allowed");
        registerCode(STRING_TOO_SHORT             , "String shorter than allowed");
        registerCode(ILLEGAL_ESCAPE_SEQUENCE      , "Invalid escape sequence (second character must be between @ and _ (0x40..0x5f)");
        registerCode(ILLEGAL_BOOLEAN              , "only 0 and 1 are allowed for a boolean field");
        registerCode(BASE64_PARSING_ERROR         , "problem parsing a base64 encoded raw field");
        registerCode(ILLEGAL_CHAR_BASE64          , "illegal character found while parsing a base64 encoded raw field");
        registerCode(ARRAY_SIZE_OUT_OF_BOUNDS     , "negative item count or item count > 1000000000");
        registerCode(BAD_CLASS                    , "parsed class is not a subclass of the expected one or subclassing is not allowed");
        registerCode(BAD_TRANSMISSION_START       , "Illegal character at the start of a transmission");
        registerCode(BAD_TIMESTAMP_FRACTIONALS    , "Illegal number of fractional digits for timestamp (must be 6..9 for precision 0..3)");
        registerCode(ILLEGAL_DAY                  , "Illegal day (required: year in [1601,2399], month in [1,12], day in [1,31])");
        registerCode(ILLEGAL_TIME                 , "Illegal time (required: hour in [0,23], minute in [0,59], second in [0,59]");
        registerCode(ILLEGAL_CALENDAR_VALUE       , "Exception converting the date/time");
        registerCode(EMPTY_CHAR                   , "empty character field");
        registerCode(BAD_OBJECT_NAME              , "bad object name (must contain a dot, and not as first or last character)");
        registerCode(BAD_UUID_FORMAT              , "malformed UUID");
        registerCode(INVALID_ENUM_TOKEN           , "invalid token to instantiate enum");
        registerCode(CLASS_NOT_FOUND              , "class could not be found or instantiated");
        registerCode(WRONG_MAP_INDEX_TYPE         , "parsed index type of map mismatches expected one");
        registerCode(NULL_MAP_NOT_ALLOWED_HERE    , "parsed NULL for a required Map<>");
        registerCode(NULL_COLLECTION_NOT_ALLOWED  , "parsed NULL for a required List, Set or Array");
        registerCode(TOO_MANY_DECIMALS            , "number contains more decimal places than allowed");
        registerCode(INVALID_BACKREFERENCE        , "The serialized message contains an invalid backreference");
        registerCode(UNSUPPORTED_DATA_TYPE        , "The request field type or operation is not supported for this composer or parser");
        registerCode(EXTRA_FIELDS                 , "Extra (non-null) fields have been encountered while expecting a class terminator. Most likely your client JAR is not up to date.");
        registerCode(TOO_MANY_DIGITS              , "Number too big");
        registerCode(UNKNOW_RECORD_TYPE           , "An unmapped record type has been encountered (CSV or fixed width parser)");
        registerCode(NULL_CLASS_PQON              , "A null class name has been transferred");
        registerCode(INVALID_BASE_CLASS_REFERENCE , "A zero length class name has been transferred, referring to a field without defined base class");
        registerCode(CUSTOM_OBJECT_EXCEPTION      , "Cannot construct custom object from parsed data");
        registerCode(NUMERIC_TOO_MANY_DIGITS      , "Numeric field has more digits than specifically configured");
        registerCode(CHAR_TOO_LONG                , "Parsed a character, but got more than 1 character");
        registerCode(NUMBER_PARSING_ERROR         , "Cannot parse number");
        registerCode(BAD_CLASS_IDS                , "No class registered for factoryId/ClassId");
        registerCode(INVALID_REFERENCES           , "Could not resolve recursive references (record for index not found)");    // mapped PersistenceException
        registerCode(UNSUPPORTED_TOKEN            , "Token not yet supported (while skipping unknown data)");
        registerCode(UNSUPPORTED_COMPRESSED       , "Attempt to skip compressed data (not yet supported by parser version)");
        registerCode(JSON_EXCEPTION               , "JSON parsing exception");
        registerCode(JSON_ID                      , "Map key is not a valid JSON identifier");
        registerCode(JSON_DUPLICATE_KEY           , "Map key of JSON map occurs multiple times");
        registerCode(JSON_NO_PQON                 , "No map entry @PQON of type String found");
        registerCode(INVALID_CHAR                 , "Invalid Character: must be String type and of length 1");
        registerCode(UNSUPPORTED_CONVERSION       , "Unsupported conversion between types");
        registerCode(BINARY_TOO_LONG              , "Binary data type too long");
        registerCode(JSON_BAD_OBJECTREF           , "Invalid object reference definition (no subclassing allowed and no base type)");
        registerCode(WRONG_CLASS                  , "Got an object of a different class");
        registerCode(JSON_EXCEPTION_MAP           , "JSON parsing exception: Map<String,Object> expected");
        registerCode(JSON_EXCEPTION_ARRAY         , "JSON parsing exception: List expected");
        registerCode(JSON_EXCEPTION_OBJECT        , "JSON parsing exception: any object");
        registerCode(INVALID_ENUM_NAME            , "invalid name to instantiate enum");
        registerCode(MISSING_CLOSING_QUOTE        , "No closing quote found");
    }

    /** Creates a parser exception with an explicitly defined position and class name. */
    public MessageParserException(int errorCode, String fieldName, int characterIndex, String className, String contents) {
        super(errorCode, fieldName, className, characterIndex);
        if (contents != null && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exception {} for {}.{} at index {}, contents is <{}>", errorCode, className, fieldName, characterIndex, contents);
        }
    }

    /** Creates a parser exception with an explicitly defined position and class name. */
    public MessageParserException(int errorCode, String fieldName, int characterIndex, String className) {
        super(errorCode, fieldName, className, characterIndex);
    }

    /** Creates a parser exception for which parse position and class name will be provided by some callback. */
    public MessageParserException(int errorCode, String fieldName, String fieldContents, ParsePositionProvider parsePositionProvider) {
        this(errorCode, fieldName, parsePositionProvider.getParsePosition(), parsePositionProvider.getCurrentClassName(), fieldContents);
    }

    public MessageParserException(int errorCode) {
        super(errorCode);
    }
}
