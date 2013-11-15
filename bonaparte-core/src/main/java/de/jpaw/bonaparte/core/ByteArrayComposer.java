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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.CharTestsASCII;
/**
 * Implements the serialization for the bonaparte format into byte arrays, using the {@link de.jpaw.util.ByteBuilder ByteBuilder} class, which is similar to the well known {@link java.lang.StringBuilder StringBuilder}.
 *
 * @author Michael Bischoff
 *
 */

public class ByteArrayComposer extends ByteArrayConstants implements BufferedMessageComposer<RuntimeException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayComposer.class);
    private final boolean useCache;
    private final Map<BonaPortable,Integer> objectCache;
    private int numberOfObjectsSerialized;
    private int numberOfObjectReuses;
    
    
    // variables for serialization
    private ByteBuilder work;

    /** Creates a new ByteArrayComposer, using this classes static default Charset **/
    public ByteArrayComposer() {
        this(ObjectReuseStrategy.defaultStrategy);
    }

    /** Creates a new ByteArrayComposer, using this classes static default Charset **/
    public ByteArrayComposer(ObjectReuseStrategy reuseStrategy) {
        switch (reuseStrategy) {
        case BY_CONTENTS:
            this.objectCache = new HashMap<BonaPortable,Integer>(250);
            this.useCache = true;
            break;
        case BY_REFERENCE:
            this.objectCache = new IdentityHashMap<BonaPortable,Integer>(250);
            this.useCache = true;
            break;
        default:
            this.objectCache = null;
            this.useCache = false;
            break;
        }
        this.work = new ByteBuilder(0, getDefaultCharset());
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
    }
    
    /** Sets the current length to 0, allowing reuse of the allocated output buffer for a new message. */
    @Override
    public void reset() {
        work.setLength(0);
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
        if (useCache)
            objectCache.clear();
    }

    /** Returns the number of bytes written. */
    @Override
    public int getLength() {    // obtain the number of written bytes (composer)
        return work.length();
    }

    /** Returns the current buffer as a Java byte array. Only the first <code>getLength()</code> bytes of this buffer are valid. */
    @Override
    public byte[] getBuffer() {
        LOGGER.debug("Buffer retrieved, {} bytes written, {} object reuses", work.length(), numberOfObjectReuses);
        return work.getCurrentBuffer();
    }

    /** returns the result as a deep copy byte array of precise length of the result. */
    @Override
    public byte[] getBytes() {
        LOGGER.debug("Bytes retrieved, {} bytes written, {} object reuses", work.length(), numberOfObjectReuses);
        return work.getBytes();  // slow!
    }

    /** allows to add raw data to the produced byte array. Use this for protocol support at beginning or end of a message */
    public void addRawData(byte [] data) {
        work.append(data);
    }

    /* *************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/

    // the following two methods are provided as separate methods instead of
    // code the single command each time,
    // with the intention that they max become extended or redefined and reused
    // for CSV output to files with
    // customized separators.
    // Because this class is defined as final, I hope the JIT will inline them
    // for better performance
    // THIS IS REQUIRED ONLY LOCALLY
    private void terminateField() {
        work.append(FIELD_TERMINATOR);
    }
    protected void writeNull() {
        work.append(NULL_FIELD);
    }
    @Override
    public void writeNull(FieldDefinition di) {
        work.append(NULL_FIELD);
    }
    
    @Override
    public void writeNullCollection(FieldDefinition di) {
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
        if (doWriteCRs()) {
            work.append(RECORD_OPT_TERMINATOR);
        }
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
        if ((c < ' ') && (c != '\t')) {
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

    // character
    @Override
    public void addField(char c) {
        addCharSub(c);
        terminateField();
    }

    // ascii only (unicode uses different method). This one does a validity check now.
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        if (s != null) {
            if (di.getRestrictToAscii()) {
                // don't trust them!
                work.append(CharTestsASCII.checkAsciiAndFixIfRequired(s, di.getLength()));
            } else {
                // tak care not to break multi-Sequences
                for (int i = 0; i < s.length();) {
                    int c = s.codePointAt(i);
                    addCharSub(c);
                    i += Character.charCount(c);
                }
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
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
    public void addField(NumericElementaryDataItem di, Integer n) {
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
        if (b) {
            work.append((byte) '1');
        } else {
            work.append((byte) '0');
        }
        terminateField();
    }


    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
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
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        if (b != null) {
            b.appendBase64(work);
            terminateField();
        } else {
            writeNull();
        }
    }

    // raw
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b != null) {
            Base64.encodeToByte(work, b, 0, b.length);
            terminateField();
        } else {
            writeNull();
        }
    }

    // append a left padded ASCII String
    private void lpad(String s, int length, byte padCharacter) {
        int l = s.length();
        while (l++ < length) {
            work.append(padCharacter);
        }
        work.appendAscii(s);
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) {  // TODO: length is not needed for this one
        if (t != null) {
            int tmpValue = (10000 * t.get(Calendar.YEAR)) + (100
                    * (t.get(Calendar.MONTH) + 1)) + t.get(Calendar.DAY_OF_MONTH);
            work.appendAscii(Integer.toString(tmpValue));
            int length = di.getFractionalSeconds();
            if (length >= 0) {
                // not only day, but also time
                if (di.getHhmmss()) {
                    tmpValue = (10000 * t.get(Calendar.HOUR_OF_DAY)) + (100
                            * t.get(Calendar.MINUTE)) + t.get(Calendar.SECOND);
                } else {
                    tmpValue = (3600 * t.get(Calendar.HOUR_OF_DAY)) + (60
                            * t.get(Calendar.MINUTE)) + t.get(Calendar.SECOND);
                }
                if ((tmpValue != 0) || ((length > 0) && (t.get(Calendar.MILLISECOND) != 0))) {
                    work.append((byte) '.');
                    lpad(Integer.toString(tmpValue), 6, (byte) '0');
                    if (length > 0) {
                        // add milliseconds
                        tmpValue = t.get(Calendar.MILLISECOND);
                        if (tmpValue != 0) {
                            lpad(Integer.toString(tmpValue), 3, (byte) '0');
                        }
                    }
                }
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        if (t != null) {
            int [] values = t.getValues();   // 3 values: year, month, day
            int tmpValue = (10000 * values[0]) + (100 * values[1]) + values[2];
            // int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
            work.appendAscii(Integer.toString(tmpValue));
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        if (t != null) {
            int [] values = t.getValues(); // 4 values: year, month, day, millis of day
            //int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
            work.appendAscii(Integer.toString((10000 * values[0]) + (100 * values[1]) + values[2]));
            int length = di.getFractionalSeconds();
            if (length >= 0) {
                // not only day, but also time
                //tmpValue = 10000 * t.getHourOfDay() + 100 * t.getMinuteOfHour() + t.getSecondOfMinute();
                if (length > 0 ? (values[3] != 0) : ((values[3] / 1000) != 0)) {
                    work.append((byte) '.');
                    if (di.getHhmmss()) {
                        int tmpValue = values[3] / 60000; // minutes and hours
                        tmpValue = (100 * (tmpValue / 60)) + (tmpValue % 60);
                        lpad(Integer.toString((tmpValue * 100) + ((values[3] % 60000) / 1000)), 6, (byte) '0');
                    } else {
                        lpad(Integer.toString(values[3] / 1000), 6, (byte) '0');
                    }
                    if (length > 0) {
                        // add milliseconds
                        int milliSeconds = values[3] % 1000;
                        if (milliSeconds != 0) {
                            lpad(Integer.toString(milliSeconds), 3, (byte) '0');
                        }
                    }
                }
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void startMap(int currentMembers, int indexID) {
        work.append(MAP_BEGIN);
        addField(indexID);
        addField(currentMembers);
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) {
        work.append(ARRAY_BEGIN);
        addField(currentMembers);
    }

    @Override
    public void terminateArray() {
        work.append(ARRAY_TERMINATOR);
    }

    @Override
    public void terminateMap() {
        work.append(ARRAY_TERMINATOR);
    }
    
    @Override
    public void startObject(BonaPortable obj) {
        work.append(OBJECT_BEGIN);
        addField(OBJECT_CLASS, obj.get$PQON());
        addField(REVISION_META, obj.get$Revision());
    }
    

    @Override
    public void addField(BonaPortable obj) {
        if (obj == null) {
            writeNull();
        } else {
            if (useCache) {
                Integer previousIndex = objectCache.get(obj);
                if (previousIndex != null) {
                    // reuse this instance
                    work.append(OBJECT_AGAIN);
                    addField(previousIndex.intValue());
                    ++numberOfObjectReuses;
                    return;
                }
                // fall through
            }
            // start a new object
            startObject(obj);

            // do all fields (now includes terminator)
            obj.serializeSub(this);
            if (useCache) {
                // add the new object to the cache of known objects
                objectCache.put(obj, Integer.valueOf(numberOfObjectsSerialized++));
            }            
        }
    }

    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, NumericElementaryDataItem ord, Enum<?> n) {
        if (n == null)
            writeNull(ord);
        else
            addField(n.ordinal());
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) {
        addField(token, n.getToken());
    }

}
