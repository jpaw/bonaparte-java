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

    private static final int OFFSET  = (CL_PARSER_ERROR         * CLASSIFICATION_FACTOR) + 17000;
    private static final int OFFSET3 = (CL_PARAMETER_ERROR      * CLASSIFICATION_FACTOR) + 17000;
    private static final int OFFSET8 = (CL_INTERNAL_LOGIC_ERROR * CLASSIFICATION_FACTOR) + 17000;

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
    static public final int UNKNOW_RECORD_TYPE           = OFFSET + 46;
    static public final int NULL_CLASS_PQON              = OFFSET + 47;
    static public final int INVALID_BASE_CLASS_REFERENCE = OFFSET + 48;
    static public final int CUSTOM_OBJECT_EXCEPTION      = OFFSET + 49;
    static public final int NUMERIC_TOO_MANY_DIGITS      = OFFSET + 50;
    static public final int CHAR_TOO_LONG                = OFFSET + 51;
    static public final int NUMBER_PARSING_ERROR         = OFFSET + 52;
    static public final int BAD_CLASS_IDS                = OFFSET + 53;
    static public final int INVALID_REFERENCES           = OFFSET + 54;
    static public final int UNSUPPORTED_TOKEN            = OFFSET + 55;
    static public final int UNSUPPORTED_COMPRESSED       = OFFSET + 56;
    static public final int JSON_EXCEPTION               = OFFSET + 57;
    static public final int JSON_ID                      = OFFSET + 58;
    static public final int JSON_DUPLICATE_KEY           = OFFSET + 59;
    static public final int JSON_NO_PQON                 = OFFSET + 60;
    static public final int INVALID_CHAR                 = OFFSET + 61;
    static public final int UNSUPPORTED_CONVERSION       = OFFSET + 62;
    static public final int BINARY_TOO_LONG              = OFFSET + 63;
    static public final int JSON_BAD_OBJECTREF           = OFFSET8 + 64;
    static public final int WRONG_CLASS                  = OFFSET + 65;
    

    static {
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
        codeToDescription.put(ILLEGAL_DAY                  , "Illegal day (required: year in [1601,2399], month in [1,12], day in [1,31])");
        codeToDescription.put(ILLEGAL_TIME                 , "Illegal time (required: hour in [0,23], minute in [0,59], second in [0,59]");
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
        codeToDescription.put(UNSUPPORTED_DATA_TYPE        , "The request field type or operation is not supported for this composer or parser");
        codeToDescription.put(EXTRA_FIELDS                 , "Extra (non-null) fields have been encountered while expecting a class terminator. Most likely your client JAR is not up to date.");
        codeToDescription.put(TOO_MANY_DIGITS              , "Number too big");
        codeToDescription.put(UNKNOW_RECORD_TYPE           , "An unmapped record type has been encountered (CSV or fixed width parser)");
        codeToDescription.put(NULL_CLASS_PQON              , "A null class name has been transferred");
        codeToDescription.put(INVALID_BASE_CLASS_REFERENCE , "A zero length class name has been transferred, referring to a field without defined base class");
        codeToDescription.put(CUSTOM_OBJECT_EXCEPTION      , "Cannot construct custom object from parsed data");
        codeToDescription.put(NUMERIC_TOO_MANY_DIGITS      , "Numeric field has more digits than specifically configured");
        codeToDescription.put(CHAR_TOO_LONG                , "Parsed a character, but got more than 1 character");
        codeToDescription.put(NUMBER_PARSING_ERROR         , "Cannot parse number");
        codeToDescription.put(BAD_CLASS_IDS                , "No class registered for factoryId/ClassId");
        codeToDescription.put(INVALID_REFERENCES           , "Could not resolve recursive references (record for index not found)");    // mapped PersistenceException
        codeToDescription.put(UNSUPPORTED_TOKEN            , "Token not yet supported (while skipping unknown data)");
        codeToDescription.put(UNSUPPORTED_COMPRESSED       , "Attempt to skip compressed data (not yet supported by parser version)");
        codeToDescription.put(JSON_EXCEPTION               , "JSON parsing exception");
        codeToDescription.put(JSON_ID                      , "Map key is not a valid JSON identifier");
        codeToDescription.put(JSON_DUPLICATE_KEY           , "Map key of JSON map occurs multiple times");
        codeToDescription.put(JSON_NO_PQON                 , "No map entry $PQON of type String found");
        codeToDescription.put(INVALID_CHAR                 , "Invalid Character: must be String type and of length 1");
        codeToDescription.put(UNSUPPORTED_CONVERSION       , "Unsupported conversion between types");
        codeToDescription.put(BINARY_TOO_LONG              , "Binary data type too long");
        codeToDescription.put(JSON_BAD_OBJECTREF           , "Invalid object reference definition (no subclassing allowed and no base type)");
        codeToDescription.put(WRONG_CLASS                  , "Got an object of a different class");
    }

    /** Creates a parser exception with an explicitly defined position and class name. */
    public MessageParserException(int errorCode, String fieldName, int characterIndex, String className, String contents) {
        super(errorCode, (className == null ? "?" : className)
                + "." + (fieldName == null ? "?" : fieldName)
                + (characterIndex >= 0 ? " at pos " + characterIndex : "")
                + (contents == null ? "<" + contents + ">" : ""));
        this.characterIndex = characterIndex;
        this.fieldName = fieldName;
        this.className = className;
    }

    /** Creates a parser exception with an explicitly defined position and class name. */
    public MessageParserException(int errorCode, String fieldName, int characterIndex, String className) {
        this(errorCode, fieldName, characterIndex, className, null);
    }

    /** Creates a parser exception for which parse position and class name will be provided by some callback. */
    public MessageParserException(int errorCode, String fieldName, String fieldContents, ParsePositionProvider parsePositionProvider) {
        this(errorCode, fieldName, parsePositionProvider.getParsePosition(), parsePositionProvider.getCurrentClassName(), fieldContents);
    }

    public MessageParserException(int errorCode) {
        this(errorCode, null, -1, null);
    }

    // some boilerplate code to retrieve exception properties
    public int getCharacterIndex() {
        return characterIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getClassName() {
        return className;
    }

}
