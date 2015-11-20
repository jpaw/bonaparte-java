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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.util.BigDecimalTools;
import de.jpaw.util.ByteArray;

/**
 * The CompactByteArrayParser class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implementation of the MessageParser for the binary compact protocol, using byte arrays.
 */

public class CompactByteArrayParser extends AbstractCompactParser<MessageParserException> {
    private int parseIndex;
    private int messageLength;
    private byte [] inputdata;

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static <T extends BonaPortable> T unmarshal(byte [] x, ObjectReference di, Class<T> expectedClass) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new CompactByteArrayParser(x, 0, -1).readObject(di, expectedClass);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static Object unmarshalElement(byte [] x, ObjectReference di) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new CompactByteArrayParser(x, 0, -1).readElement(di);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static List<Object> unmarshalArray(byte [] x, ObjectReference di) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new CompactByteArrayParser(x, 0, -1).readArray(di);
    }

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static Map<String, Object> unmarshalJson(byte [] x, ObjectReference di) throws MessageParserException {
        if (x == null || x.length == 0)
            return null;
        return new CompactByteArrayParser(x, 0, -1).readJson(di);
    }

    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src, int offset, int length) {
        inputdata = src;
        parseIndex = offset;
        messageLength = length < 0 ? src.length : length;
        clearCache();
    }

    /** Assigns a new source to subsequent parsing operations. */
    public final void setSource(byte [] src) {
        inputdata = src;
        parseIndex = 0;
        messageLength = src.length;
    }

    /** Create a processor for parsing a buffer. */
    public CompactByteArrayParser(byte [] buffer, int offset, int length) {
        super();
        inputdata = buffer;
        parseIndex = offset;
        messageLength = length < 0 ? inputdata.length : length; // -1 means full array size / until end of data
        currentClass = "N/A";
    }

    @Override
    protected int getParseIndex() {
        return parseIndex;
    }

    /**************************************************************************************************
     * Deserialization goes here. Code below does not use the ByteBuilder class,
     * but reads from the byte[] directly
     **************************************************************************************************/

    @Override
    protected MessageParserException newMPE(int errorCode, String msg) {
        return new MessageParserException(errorCode, msg, parseIndex, currentClass);
    }

    @Override
    protected BonaPortable createObject(String classname) throws MessageParserException {           // same method - overloading required for possible exception mapping
        return BonaPortableFactory.createObject(classname);
    }

    @Override
    protected BigDecimal checkAndScale(BigDecimal num, NumericElementaryDataItem di) throws MessageParserException {
        return BigDecimalTools.checkAndScale(num, di, parseIndex, currentClass);
    }

    // special method only in the ByteArray version
    protected void require(int length) throws MessageParserException {
        if (parseIndex + length > messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, null);
        }
    }

    @Override
    protected boolean atEnd() throws MessageParserException {
        return parseIndex >= messageLength;
    }
    
    @Override
    protected int needToken() throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, null);
        }
        return inputdata[parseIndex++] & 0xff;
    }

    @Override
    protected void needToken(int c) throws MessageParserException {
        if (parseIndex >= messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, String.format("(expected 0x%02x)", c));
        }
        int d = inputdata[parseIndex++] & 0xff;
        if (c != d) {
            throw newMPE(MessageParserException.UNEXPECTED_CHARACTER, String.format("(expected 0x%02x, got 0x%02x)", c, d));
        }
    }
    

    @Override
    protected void pushback(int c) {
        // ignore c, just decrement the position
        --parseIndex;
    } 

    @Override
    protected char readChar() throws MessageParserException {
        require(2);
        char cc = (char)(((inputdata[parseIndex] & 0xff) << 8) | (inputdata[parseIndex+1] & 0xff));
        parseIndex += 2;
        return cc;
    }

    @Override
    protected int readFixed2ByteInt() throws MessageParserException {
        require(2);
        int nn = inputdata[parseIndex++] << 8;
        return nn | inputdata[parseIndex++] & 0xff;
    }
    
    @Override
    protected int readFixed3ByteInt() throws MessageParserException {
        require(3);
        int nn = inputdata[parseIndex++] << 16;             // does sign-extend as required
        nn |= (inputdata[parseIndex++] & 0xff) << 8;
        nn |= inputdata[parseIndex++] & 0xff;
        return nn;
    }
    
    @Override
    protected int readFixed4ByteInt() throws MessageParserException {
        require(4);
        int nn = (inputdata[parseIndex++] & 0xff) << 24;
        nn |= (inputdata[parseIndex++] & 0xff) << 16;
        nn |= (inputdata[parseIndex++] & 0xff) << 8;
        nn |= inputdata[parseIndex++] & 0xff;
        return nn;
    }

    @Override
    protected long readFixed6ByteLong() throws MessageParserException {
        require(6);
        int nn1 = inputdata[parseIndex++] << 8;
        nn1 |= inputdata[parseIndex++] & 0xff;
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }
    @Override
    protected long readFixed8ByteLong() throws MessageParserException {
        int nn1 = readFixed4ByteInt();
        int nn2 = readFixed4ByteInt();
        return ((long)nn1 << 32) | (nn2 & 0xffffffffL);
    }

    @Override
    protected byte [] readBytes(int len) throws MessageParserException {
        if (len == 0)
            return EMPTY_BYTE_ARRAY;
        require(len);
        byte [] data = new byte [len];
        System.arraycopy(inputdata, parseIndex, data, 0, len);
        parseIndex += len;
        return data;
    }
    
    @Override
    protected ByteArray readByteArray(int len) throws MessageParserException {
        if (len > 0) {
            ByteArray result = new ByteArray(inputdata, parseIndex, len);
            parseIndex += len;
            return result;
        }
        return ByteArray.ZERO_BYTE_ARRAY;
    }

    // faster implementation: expect that new String (char []) is faster than anything with charset encoder, because the loop in this code will be inline
    @Override
    protected String readISO(int len) throws MessageParserException {
        require(len);
        char data [] = new char [len];
        for (int i = 0; i < len; ++i)
            data[i] = (char) (0xff & (char)inputdata[parseIndex++]);
        return new String(data);
//        byte data [] = readBytes(len);
//        return new String(data, "ISO-8859-1");
    }

    // read len characters
    @Override
    protected String readUTF16(int len) throws MessageParserException {
        len *= 2;
        require(len);
        try {
            String result = new String(inputdata, parseIndex, len, CHARSET_UTF16);
            parseIndex += len;
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);      // should never happen, CHARSET_UTF16 is guaranteed to exist
        }
    }

    // read len bytes
    @Override
    protected String readUTF8(int len) throws MessageParserException {
        require(len);
        try {
            String result = new String(inputdata, parseIndex, len, CHARSET_UTF8);
            parseIndex += len;
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);      // should never happen, CHARSET_UTF16 is guaranteed to exist
        }
    }

    
    @Override
    protected void skipBytes(int howMany) throws MessageParserException {
        if (parseIndex + howMany >= messageLength) {
            throw newMPE(MessageParserException.PREMATURE_END, String.format("(while skipping  %d characters from pos %d (0x%04x))", howMany, parseIndex, parseIndex));
        }
        parseIndex += howMany;
    }
}
