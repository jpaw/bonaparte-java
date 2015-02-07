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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteTestsASCII;
/**
 * The ByteArrayParser class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implementation of the MessageParser, using byte arrays.
 */

public class ByteArrayParser extends ByteArrayConstants implements MessageParser<MessageParserException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayParser.class);
    private int parseIndex;
    private int messageLength;
    private byte [] inputdata;
    private String currentClass;
    private final boolean useCache = true;
    private List<BonaPortable> objects;
    
    protected final StringParserUtil stringParser = new StringParserUtil(new ParsePositionProvider() {

        @Override
        public int getParsePosition() {
            return parseIndex;
        }

        @Override
        public String getCurrentClassName() {
            return currentClass;
        }
    });

    
    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static <T extends BonaPortable> T unmarshal(byte [] x, ObjectReference di, Class<T> expectedClass) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new ByteArrayParser(x, 0, -1).readObject(di, expectedClass);
    }
    
    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src, int offset, int length) {
        inputdata = src;
        parseIndex = offset;
        messageLength = length;
        if (useCache)
            objects.clear();
    }
    
    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src) {
        inputdata = src;
        parseIndex = 0;
        messageLength = src.length;
        if (useCache)
            objects.clear();
    }
    
    /** Create a processor for parsing a buffer. */
    public ByteArrayParser(byte [] buffer, int offset, int length) {
        inputdata = buffer;
        parseIndex = offset;
        messageLength = length < 0 ? inputdata.length : length; // -1 means full array size
        currentClass = "N/A";
        if (useCache)
            objects = new ArrayList<BonaPortable>(60);
    }


    /**************************************************************************************************
     * Deserialization goes here. Code below does not use the ByteBuilder class,
     * but reads from the byte[] directly
     **************************************************************************************************/

    private byte needToken() throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END, null, parseIndex, currentClass);
        }
        return inputdata[parseIndex++];
    }

    private void needToken(byte c) throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw new MessageParserException(MessageParserException.PREMATURE_END,
                    String.format("(expected 0x%02x)", (int)c), parseIndex, currentClass);
        }
        byte d = inputdata[parseIndex++];
        if (c != d) {
            throw new MessageParserException(MessageParserException.UNEXPECTED_CHARACTER,
                    String.format("(expected 0x%02x, got 0x%02x)", (int)c, (int)d), parseIndex, currentClass);
        }
    }

    /* If byte c occurs, eat it */
    private void skipByte(byte c) throws MessageParserException {
        if ((parseIndex < messageLength) && (inputdata[parseIndex] == c)) {
            ++parseIndex;
        }
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(FieldDefinition di) throws MessageParserException {
        return checkForNull(di.getName(), di.getIsRequired());
    }
    protected int readInteger(String fieldname) throws MessageParserException {
        checkForNull(fieldname, true);
        return Integer.parseInt(nextIndexParseAscii(fieldname, false, false, false));
    }

    // check for Null called for field members inside a class
    private boolean checkForNull(String fieldname, boolean isRequired) throws MessageParserException {
        byte c = needToken();
        if (c == NULL_FIELD) {
            if (!isRequired) {
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_EXPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        if ((c == PARENT_SEPARATOR) || (c == ARRAY_TERMINATOR) || (c == OBJECT_TERMINATOR)) {
            if (!isRequired) {
                // uneat it
                --parseIndex;
                return true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_IMPLICIT_NULL, fieldname, parseIndex, currentClass);
            }
        }
        --parseIndex;
        return false;
    }

    private void skipLeadingSpaces() {
        while (parseIndex < messageLength) {
            byte c = inputdata[parseIndex];
            if ((c != ' ') && (c != '\t')) {
                break;
            }
            // skip leading blanks
            ++parseIndex;
        }
    }

    private void skipNulls() {
        while (parseIndex < messageLength) {
            byte c = inputdata[parseIndex];
            if (c != NULL_FIELD) {
                break;
            }
            // skip trailing NULL objects
            ++parseIndex;
        }
    }

    private String nextIndexParseAscii(String fieldname, boolean allowSign, boolean allowDecimalPoint, boolean allowExponent) throws MessageParserException {
        final int BUFFER_SIZE = 40;
        boolean allowSignNextIteration = false;
        boolean gotAnyDigit = false;
        StringBuffer tmp = new StringBuffer(BUFFER_SIZE);

        // skipBlanks: does not hurt!
        skipLeadingSpaces();
        if ((parseIndex < messageLength) && (inputdata[parseIndex] == PLUS_SIGN)) {
            // allow positive sign in any case (but not followed by a minus)
            ++parseIndex;
            allowSign = false;
        }
        while (parseIndex < messageLength) {
            byte c = inputdata[parseIndex];
            if (c == FIELD_TERMINATOR) {
                if (!gotAnyDigit) {
                    throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
                }
                ++parseIndex;  // eat it!
                return tmp.toString();
            }

            if (c == MINUS_SIGN) {
                if (!allowSign) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_SIGN, fieldname, parseIndex, currentClass);
                }
            } else if (c == DECIMAL_POINT) {
                if (!allowDecimalPoint) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_DECIMAL_POINT, fieldname, parseIndex, currentClass);
                }
                allowDecimalPoint = false;  // no 2 in a row allowed
            } else if ((c == 'e') || (c == 'E')) {
                if (!allowExponent) {
                    throw new MessageParserException(MessageParserException.SUPERFLUOUS_EXPONENT, fieldname, parseIndex, currentClass);
                }
                if (!gotAnyDigit) {
                    throw new MessageParserException(MessageParserException.NO_DIGITS_FOUND, fieldname, parseIndex, currentClass);
                }
                allowSignNextIteration = true;
                allowExponent = false;
                allowDecimalPoint = false;
            } else if (ByteTestsASCII.isAsciiDigit(c)) {
                gotAnyDigit = true;
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_NOT_NUMERIC, fieldname, parseIndex, currentClass);
            }
            if (tmp.length() >= BUFFER_SIZE) {
                throw new MessageParserException(MessageParserException.NUMERIC_TOO_LONG, fieldname, parseIndex, currentClass);
            }
            tmp.appendCodePoint(c);
            ++parseIndex;
            allowSign = allowSignNextIteration;
            allowSignNextIteration = false;
        }
        // end of message without appropriate terminator character
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
    }

    @Override
    public Character readCharacter(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readCharacter(di, readString(di.getName(), di.getIsRequired(), 1, false, false, true, true));
    }

    
    // readString does the job for Unicode as well as ASCII
    @Override
    public String readString(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readString(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate(), di.getAllowControlCharacters(), true);
    }
    
    // readString does the job for Unicode as well as ASCII, but only used for Unicode (have an optimized version for ASCII)
    protected String readString(String fieldname, boolean isRequired, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws MessageParserException {
        if (checkForNull(fieldname, isRequired)) {
            return null;
        }
        if (doTrim) {
            // skip leading spaces
            skipLeadingSpaces();
        }
        // parse the data and check for escape sequences
        boolean escapeUsed = false;
        int currentIndex = parseIndex;
        int lastNonBlank = parseIndex-1;
        while (currentIndex < messageLength) {
            byte b = inputdata[currentIndex];
            if (b == FIELD_TERMINATOR) {
                // regular end of string
                String result;
                if (!doTrim) {
                    // ignore last nonblank
                    lastNonBlank = currentIndex-1;
                }
                if (lastNonBlank < parseIndex) {
                    // zero character string
                    result = EMPTY_STRING;
                } else if (!escapeUsed) {
                    // simple (& fast?) way to do this
                    result = new String(inputdata, parseIndex, (lastNonBlank-parseIndex)+1, getCharset());
                } else if (!allowCtrls || !allowUnicode) {
                    // check if escapes were allowed in the first place...   NO!
                    throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_CTRL, fieldname, parseIndex, currentClass);
                } else {
                    // the ugly part. Must run through it again, replacing
                    // escapes, need a temporary buffer
                    byte[] tmp = new byte[(lastNonBlank - parseIndex) + 1]; // preliminary
                    // length,
                    // escapes
                    // will
                    // reduce
                    // this
                    int i = 0;
                    while (parseIndex <= lastNonBlank) {
                        b = inputdata[parseIndex++];
                        if (b == ESCAPE_CHAR) {
                            b = inputdata[parseIndex++];
                            if ((b < 0x40) || (b >= 0x60)) {
                                throw new MessageParserException(MessageParserException.ILLEGAL_ESCAPE_SEQUENCE,
                                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
                            }
                            b -= 0x40;
                        }
                        tmp[i++] = b;
                    }
                    result = new String(tmp, 0, i, getCharset());
                }
                parseIndex = currentIndex + 1;
                // length checks
                if (length > 0) {
                    // have limits on max size
                    if (result.length() > length) {
                        if (doTruncate) {
                            result = result.substring(0, length);
                        } else {
                            throw new MessageParserException(MessageParserException.STRING_TOO_LONG,
                                    String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, result.toString()),
                                    parseIndex, currentClass);
                        }
                    }
                }
                return result;
            }
            if (b == ESCAPE_CHAR) {
                escapeUsed = true;
            } else if ((b != ' ') && (b != '\t')) {
                lastNonBlank = currentIndex;
            } else if (!allowUnicode && !ByteTestsASCII.isAsciiPrintable(b)) {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_ASCII,
                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
            }
            ++currentIndex;
        }
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
    }

    // specialized version without charset conversion
    @Override
    public String readAscii(AlphanumericElementaryDataItem di) throws MessageParserException {
        return readAscii(di.getName(), di.getIsRequired(), di.getLength(), di.getDoTrim(), di.getDoTruncate());
    }
    
    protected String readAscii(String fieldname, boolean isRequired, int length, boolean doTrim, boolean doTruncate) throws MessageParserException {
        if (checkForNull(fieldname, isRequired)) {
            return null;
        }
        if (doTrim) {
            // skip leading spaces
            skipLeadingSpaces();
        }
        StringBuilder tmp = new StringBuilder(length);
        while (parseIndex < messageLength) {
            byte b = inputdata[parseIndex++];
            if (b == FIELD_TERMINATOR) {
                // regular end of string. Check possible trimming
                int resultLength = tmp.length();
                if (doTrim) {
                    int lastNonBlank = resultLength;
                    char c;
                    while ((lastNonBlank > 0)
                            && (((c = tmp.charAt(lastNonBlank-1)) ==  ' ') || (c == '\t'))) {
                        --lastNonBlank;
                    }
                    if (lastNonBlank < resultLength) {
                        resultLength = lastNonBlank;
                        tmp.setLength(lastNonBlank);
                    }
                }
                if ((length > 0) && (resultLength > length)) {
                    if (doTruncate) {
                        tmp.setLength(length);
                    } else {
                        throw new MessageParserException(MessageParserException.STRING_TOO_LONG,
                                String.format("(exceeds length %d for %s, got so far %s)", length, fieldname, tmp.toString()),
                                parseIndex, currentClass);
                    }
                }
                if (resultLength == 0) {
                    return EMPTY_STRING;
                } else {
                    return tmp.toString();
                }
            }
            if (b == ESCAPE_CHAR) {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_CTRL, fieldname, parseIndex, currentClass);
            } else if (!ByteTestsASCII.isAsciiPrintable(b)) {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_ASCII,
                        String.format("(found 0x%02x for %s)", (int)b, fieldname), parseIndex, currentClass);
            }
            tmp.append((char)b);
        }
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, fieldname, parseIndex, currentClass);
    }

    @Override
    public Boolean readBoolean(MiscElementaryDataItem di) throws MessageParserException {
        boolean result;
        if (checkForNull(di)) {
            return null;
        }
        byte c = needToken();
        if (c == '0') {
            result = false;
        } else if (c == '1') {
            result = true;
        } else {
            throw new MessageParserException(MessageParserException.ILLEGAL_BOOLEAN,
                    String.format("(found 0x%02x for %s)", (int)c, di.getName()), parseIndex, currentClass);
        }
        needToken(FIELD_TERMINATOR);
        return Boolean.valueOf(result);
    }

    @Override
    public ByteArray readByteArray(BinaryElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        skipLeadingSpaces();
        // compute length of data and perform (rough) check for illegal characters
        int i = parseIndex;
        // find next occurence of field terminator
        while (i < messageLength) {
            byte b = inputdata[i++];
            if (b == FIELD_TERMINATOR) {
                ByteArray result = ByteArray.fromBase64(inputdata, parseIndex, i-parseIndex-1);
                if (result == null) {
                    throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, di.getName(), parseIndex, currentClass);
                }
                parseIndex = i;
                return result;
            }
            if ((b >= '0') && (b <= 'z')) {
                // OK
            } else if ((b == '+') || (b == '/') || (b == '=')) {
                // OK
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_BASE64,
                        String.format("(found 0x%02x for %s)", (int)b, di.getName()), parseIndex, currentClass);
            }
        }
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, di.getName(), parseIndex, currentClass);
    }

    @Override
    public byte[] readRaw(BinaryElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        skipLeadingSpaces();
        // compute length of data and perform (rough) check for illegal characters
        int i = parseIndex;
        // find next occurence of field terminator
        while (i < messageLength) {
            byte b = inputdata[i++];
            if (b == FIELD_TERMINATOR) {
                byte [] result;
                // have the subset of data
                if (i == (parseIndex+1)) {
                    // zero length
                    result = new byte[0];
                } else {
                    result = Base64.decode(inputdata, parseIndex, i-parseIndex-1);
                    if (result == null) {
                        throw new MessageParserException(MessageParserException.BASE64_PARSING_ERROR, di.getName(), parseIndex, currentClass);
                    }
                }
                parseIndex = i;
                return result;
            }
            if ((b >= '0') && (b <= 'z')) {
                // OK
            } else if ((b == '+') || (b == '/') || (b == '=')) {
                // OK
            } else {
                throw new MessageParserException(MessageParserException.ILLEGAL_CHAR_BASE64,
                        String.format("(found 0x%02x for %s)", (int)b, di.getName()), parseIndex, currentClass);
            }
        }
        throw new MessageParserException(MessageParserException.MISSING_TERMINATOR, di.getName(), parseIndex, currentClass);
    }

    @Override
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readDayTime(di, nextIndexParseAscii(di.getName(), false, di.getFractionalSeconds() >= 0, false));
    }
    
    @Override
    public LocalDate readDay(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readDay(di, nextIndexParseAscii(di.getName(), false, false, false));  // parse an unsigned numeric string without exponent
    }

    @Override
    public LocalTime readTime(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readTime(di, nextIndexParseAscii(di.getName(), false, di.getFractionalSeconds() > 0, false));  // parse an unsigned numeric string without exponent
    }


    @Override
    public Instant readInstant(TemporalElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readInstant(di, nextIndexParseAscii(di.getName(), false, true, false));  // parse an unsigned numeric string without exponent
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {  // check it separately in order to give a distinct error message
            if (di.getIsAggregateRequired())
                throw new MessageParserException(MessageParserException.NULL_MAP_NOT_ALLOWED_HERE, fieldname, parseIndex, currentClass);
            return -1;
        }
        needToken(MAP_BEGIN);
        int foundIndexType = readInteger(fieldname);
        if (foundIndexType != di.getMapIndexType().ordinal()) {
            throw new MessageParserException(MessageParserException.WRONG_MAP_INDEX_TYPE,
                    String.format("(got %d, expected for %s)", foundIndexType, di.getMapIndexType(), fieldname), parseIndex, currentClass);
        }
        int n = readInteger(fieldname);
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfChild) throws MessageParserException {
        String fieldname = di.getName();
        if (checkForNull(fieldname, false)) {
            if (di.getIsAggregateRequired())
                throw new MessageParserException(MessageParserException.NULL_COLLECTION_NOT_ALLOWED, fieldname, parseIndex, currentClass);
            return -1;
        }
        needToken(ARRAY_BEGIN);
        int n = readInteger(fieldname);
        if ((n < 0) || (n > 1000000000)) {
            throw new MessageParserException(MessageParserException.ARRAY_SIZE_OUT_OF_BOUNDS,
                    String.format("(got %d entries (0x%x) for %s)", n, n, fieldname), parseIndex, currentClass);
        }
        return n;
    }

    @Override
    public void parseArrayEnd() throws MessageParserException {
        needToken(ARRAY_TERMINATOR);
    }

    protected void skipOptionalBom() throws MessageParserException {
        if (parseIndex + 3 <= messageLength) {
            if (inputdata[parseIndex] == BOM1 
             && inputdata[parseIndex+1] == BOM2
             && inputdata[parseIndex+2] == BOM3) {
                parseIndex += 3;
            }
        }
    }
    
    @Override
    public BonaPortable readRecord() throws MessageParserException {
        BonaPortable result;
        skipOptionalBom();
        needToken(RECORD_BEGIN);
        needToken(NULL_FIELD); // version no
        result = readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
        skipByte(RECORD_OPT_TERMINATOR);
        needToken(RECORD_TERMINATOR);
        return result;
    }

    @Override
    public Byte readByte(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readByte(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Short readShort(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readShort(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Integer readInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readInteger(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Long readLong(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readLong(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public Float readFloat(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readFloat(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), true, true));
    }

    @Override
    public Double readDouble(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readDouble(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), true, true));
    }

    @Override
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readBigInteger(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), false, false));
    }

    @Override
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        return stringParser.readBigDecimal(di, nextIndexParseAscii(di.getName(), di.getIsSigned(), true, false));
    }

    @Override
    public void eatParentSeparator() throws MessageParserException {
        eatObjectOrParentSeparator(PARENT_SEPARATOR);
    }       
        
    public void eatObjectTerminator() throws MessageParserException {
        eatObjectOrParentSeparator(OBJECT_TERMINATOR);
    }
    
    protected void eatObjectOrParentSeparator(byte which) throws MessageParserException {
        skipNulls();  // upwards compatibility: skip extra fields if they are blank.
        byte z = needToken();
        if (z == which)
            return;   // all good
        
        // temporarily provide compatibility to 1.7.9 and back...
        if (z == PARENT_SEPARATOR) {
            // implies we have been looking for OBJECT_TERMINATOR...
            return;
        }
        
        // we have extra data and it is not null. Now the behavior depends on a parser setting
        ParseSkipNonNulls mySetting = getSkipNonNullsBehavior();
        switch (mySetting) {
        case ERROR:
            throw new MessageParserException(MessageParserException.EXTRA_FIELDS, String.format("(found byte 0x%02x)", z), parseIndex, currentClass);  
        case WARN:
            LOGGER.warn("{} at index {} parsing class {}", MessageParserException.codeToString(MessageParserException.EXTRA_FIELDS), parseIndex, currentClass);
            // fall through
        case IGNORE:
            // skip bytes until we are at end of record (bad!) (thrown by needToken()) or find the terminator
            skipUntilNext(which);
        }
    }
    
    protected void skipUntilNext(byte which) throws MessageParserException {
        byte c;
        while ((c = needToken()) != which) {
            if (c == OBJECT_BEGIN) {
                // skip nested object!
                skipUntilNext(OBJECT_TERMINATOR);
            }
        }
    }

    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws MessageParserException {
        if (checkForNull(di)) {
            return null;
        }
        boolean allowSubtypes = di.getAllowSubclasses();
        String fieldname = di.getName();
        if (useCache && parseIndex < messageLength && inputdata[parseIndex] == OBJECT_AGAIN) {
            // we reuse an object
            ++parseIndex;
            int objectIndex = readInteger(fieldname);
            if (objectIndex >= objects.size())
                throw new MessageParserException(MessageParserException.INVALID_BACKREFERENCE, String.format(
                        "at %s: requested object %d of only %d available", fieldname, objectIndex, objects.size()),
                        parseIndex, currentClass);
            BonaPortable newObject = objects.get(objects.size() - 1 - objectIndex);  // 0 is the last one put in, 1 the one before last etc...
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new MessageParserException(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
                }
            }
            return type.cast(newObject);
        } else {
            String previousClass = currentClass;
            needToken(OBJECT_BEGIN); // version not yet allowed
            String classname = readAscii(fieldname, true, 0, true, false);
            // String revision = readAscii(true, 0, false, false);
            needToken(NULL_FIELD); // version not yet allowed
            BonaPortable newObject = BonaPortableFactory.createObject(classname);
            // System.out.println("Creating new obj " + classname + " gave me " + newObject);
            // check if the object is of expected type
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!allowSubtypes || !type.isAssignableFrom(newObject.getClass())) {
                    throw new MessageParserException(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), fieldname, allowSubtypes), parseIndex, currentClass);
                }
            }
            // all good here. Parse the contents
            // if we use the cache, make the object known even before the contents has been parsed, because it may be referenced if the structure is cyclic
            if (useCache)
                objects.add(newObject);
            
            currentClass = classname;
            newObject.deserialize(this);
            eatObjectTerminator();
            currentClass = previousClass;
            return type.cast(newObject);
        }
    }

    @Override
    public List<BonaPortable> readTransmission() throws MessageParserException {
        List<BonaPortable> results = new ArrayList<BonaPortable>();
        byte c = needToken();
        if (c == TRANSMISSION_BEGIN) {
            needToken(NULL_FIELD);  // version
            // TODO: parse extensions here
            while ((c = needToken()) != TRANSMISSION_TERMINATOR) {
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
    public UUID readUUID(MiscElementaryDataItem di) throws MessageParserException {
        return stringParser.readUUID(di, readString(di.getName(), di.getIsRequired(), 36, false, false, false, false));
    }


    @Override
    public MessageParserException enumExceptionConverter(IllegalArgumentException e) {
        return new MessageParserException(MessageParserException.INVALID_ENUM_TOKEN, e.getMessage(), parseIndex, currentClass);
    }

    @Override
    public MessageParserException customExceptionConverter(String msg, Exception e) {
        return new MessageParserException(MessageParserException.CUSTOM_OBJECT_EXCEPTION, e != null ? msg + e.toString() : msg, parseIndex, currentClass);
    }


    @Override
    public void setClassName(String newClassName) {
        currentClass = newClassName;
    }


    @Override
    public <T extends AbstractXEnumBase<T>> T readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws MessageParserException {
        XEnumDefinition spec = di.getBaseXEnum();
        String scannedToken = readString(di.getName(), di.getIsRequired() && !spec.getHasNullToken(), spec.getMaxTokenLength(), true, false, false, true);
        return stringParser.readXEnum(di, factory, scannedToken);
    }
}
