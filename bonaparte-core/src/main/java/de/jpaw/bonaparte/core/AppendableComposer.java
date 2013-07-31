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

import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.CharTestsASCII;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import javax.xml.bind.DatatypeConverter;
/**
 * The StringBuilderComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the serialization for the bonaparte format using a generic Appendable.
 *          Unfortunately this generalization implies declaring a "throws IOException" for every method,
 *          even though a specific implementation using StringBuilder does not throw them.
 *          Java had better defined Appendable using Generics, allowing to specify the Exception thrown, as
 *          Appendable<? extends Throwable>. See complaints also here: http://confluence.jetbrains.com/display/Kotlin/Exceptions,
 *          however in this case it's not due to a language constraint but rather to poor definition of the interface Appendable.
 */

public class AppendableComposer extends StringBuilderConstants implements MessageComposer<IOException> {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayComposer.class);
    private final boolean useCache;
    private final Map<BonaPortable,Integer> objectCache;
    private int numberOfObjectsSerialized;
    private int numberOfObjectReuses;
    // variables set by constructor
    private final Appendable work;

    public AppendableComposer(Appendable work) {
        this(work, ObjectReuseStrategy.defaultStrategy);
    }

    /** Creates a new ByteArrayComposer, using this classes static default Charset **/
    public AppendableComposer(Appendable work, ObjectReuseStrategy reuseStrategy) {
        switch (reuseStrategy) {
        case BY_CONTENTS:
            this.objectCache = new HashMap<BonaPortable, Integer>(250);
            this.useCache = true;
            break;
        case BY_REFERENCE:
            this.objectCache = new IdentityHashMap<BonaPortable, Integer>(250);
            this.useCache = true;
            break;
        default:
            this.objectCache = null;
            this.useCache = false;
            break;
        }
        this.work = work;
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
    }

    /**************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/

    /** allows to add raw data to the produced byte array. Use this for protocol support at beginning or end of a message
     * @throws IOException */
    public final void addRawData(String data) throws IOException {
        if (data != null)
            work.append(data);
    }

    protected void terminateField() throws IOException {
        work.append(FIELD_TERMINATOR);
    }
    @Override
    public void writeNull() throws IOException {
        work.append(NULL_FIELD);
    }

    @Override
    public void startTransmission() throws IOException {
        work.append(TRANSMISSION_BEGIN);
        writeNull();    // blank version number
    }
    @Override
    public void terminateTransmission() throws IOException {
        work.append(TRANSMISSION_TERMINATOR);
        work.append(TRANSMISSION_TERMINATOR2);
    }

    @Override
    public void terminateRecord() throws IOException {
        if (doWriteCRs()) {
            work.append(RECORD_OPT_TERMINATOR);
        }
        work.append(RECORD_TERMINATOR);
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
        work.append(PARENT_SEPARATOR);
    }

    @Override
    public void startRecord() throws IOException {
        work.append(RECORD_BEGIN);
        writeNull();  // blank version number
    }

    @Override
    public void writeRecord(BonaPortable o) throws IOException {
        startRecord();
        addField(o);
        terminateRecord();
    }

    private void addCharSub(char c) throws IOException {
        if ((c >= 0) && (c < ' ') && (c != '\t')) {
            work.append(ESCAPE_CHAR);
            work.append((char)(c + '@'));
        } else {
            work.append(c);
        }
    }

    // field type specific output functions
    @Override
    public void addUnicodeString(String s, int length, boolean allowCtrls) throws IOException {
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
    public void addField(char c) throws IOException {
        addCharSub(c);
        terminateField();
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(String s, int length) throws IOException {
        if (s != null) {
            work.append(CharTestsASCII.checkAsciiAndFixIfRequired(s, length));
            terminateField();
        } else {
            writeNull();
        }
    }

    // decimal
    @Override
    public void addField(BigDecimal n, int length, int decimals,
            boolean isSigned) throws IOException {
        if (n != null) {
            work.append(n.toPlainString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // byte
    @Override
    public void addField(byte n) throws IOException {
        work.append(Byte.toString(n));
        terminateField();
    }
    // short
    @Override
    public void addField(short n) throws IOException {
        work.append(Short.toString(n));
        terminateField();
    }
    // integer
    @Override
    public void addField(int n) throws IOException {
        work.append(Integer.toString(n));
        terminateField();
    }

    // int(n)
    @Override
    public void addField(Integer n, int length, boolean isSigned) throws IOException {
        if (n != null) {
            work.append(n.toString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // long
    @Override
    public void addField(long n) throws IOException {
        work.append(Long.toString(n));
        terminateField();
    }

    // boolean
    @Override
    public void addField(boolean b) throws IOException {
        if (b) {
            work.append('1');
        } else {
            work.append('0');
        }
        terminateField();
    }

    // float
    @Override
    public void addField(float f) throws IOException {
        work.append(Float.toString(f));
        terminateField();
    }

    // double
    @Override
    public void addField(double d) throws IOException {
        work.append(Double.toString(d));
        terminateField();
    }

    // UUID
    @Override
    public void addField(UUID n) throws IOException {
        if (n != null) {
            work.append(n.toString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(ByteArray b, int length) throws IOException {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length() * 2) + 4, null);
            Base64.encodeToByte(tmp, b.getBytes(), 0, b.length());
            work.append(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
            //work.append(DatatypeConverter.printBase64Binary(b));
            //work.append(DatatypeConverter.printHexBinary(b));
            terminateField();
        } else {
            writeNull();
        }
    }

    // raw
    @Override
    public void addField(byte[] b, int length) throws IOException {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length * 2) + 4, null);
            Base64.encodeToByte(tmp, b, 0, b.length);
            work.append(new String(tmp.getCurrentBuffer(), 0, tmp.length()));
            //work.append(DatatypeConverter.printHexBinary(b));
            terminateField();
        } else {
            writeNull();
        }
    }

    // append a left padded String
    private void lpad(String s, int length, char padCharacter) throws IOException {
        int l = s.length();
        while (l++ < length) {
            work.append(padCharacter);
        }
        work.append(s);
    }

    // converters for DAY und TIMESTAMP
    @Override
    public void addField(Calendar t, boolean hhmmss, int length) throws IOException {  // TODO: length is not needed for this one
        if (t != null) {
            int tmpValue = (10000 * t.get(Calendar.YEAR)) + (100
                    * (t.get(Calendar.MONTH) + 1)) + t.get(Calendar.DAY_OF_MONTH);
            work.append(Integer.toString(tmpValue));
            if (length >= 0) {
                // not only day, but also time
                if (hhmmss) {
                    tmpValue = (10000 * t.get(Calendar.HOUR_OF_DAY)) + (100
                            * t.get(Calendar.MINUTE)) + t.get(Calendar.SECOND);
                } else {
                    tmpValue = (3600 * t.get(Calendar.HOUR_OF_DAY)) + (60
                            * t.get(Calendar.MINUTE)) + t.get(Calendar.SECOND);
                }
                if ((tmpValue != 0) || ((length > 0) && (t.get(Calendar.MILLISECOND) != 0))) {
                    work.append('.');
                    lpad(Integer.toString(tmpValue), 6, '0');
                    if (length > 0) {
                        // add milliseconds
                        tmpValue = t.get(Calendar.MILLISECOND);
                        if (tmpValue != 0) {
                            lpad(Integer.toString(tmpValue), 3, '0');
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
    public void addField(LocalDate t) throws IOException {
        if (t != null) {
            int [] values = t.getValues();   // 3 values: year, month, day
            int tmpValue = (10000 * values[0]) + (100 * values[1]) + values[2];
            // int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
            work.append(Integer.toString(tmpValue));
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(LocalDateTime t, boolean hhmmss, int length) throws IOException {
        if (t != null) {
            int [] values = t.getValues(); // 4 values: year, month, day, millis of day
            //int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
            work.append(Integer.toString((10000 * values[0]) + (100 * values[1]) + values[2]));
            if (length >= 0) {
                // not only day, but also time
                //tmpValue = 10000 * t.getHourOfDay() + 100 * t.getMinuteOfHour() + t.getSecondOfMinute();
                if (length > 0 ? (values[3] != 0) : ((values[3] / 1000) != 0)) {
                    work.append('.');
                    if (hhmmss) {
                        int tmpValue = values[3] / 60000; // minutes and hours
                        tmpValue = (100 * (tmpValue / 60)) + (tmpValue % 60);
                        lpad(Integer.toString((tmpValue * 100) + ((values[3] % 60000) / 1000)), 6, '0');
                    } else {
                        lpad(Integer.toString(values[3] / 1000), 6, '0');
                    }
                    if (length > 0) {
                        // add milliseconds
                        int milliSeconds = values[3] % 1000;
                        if (milliSeconds != 0) {
                            lpad(Integer.toString(milliSeconds), 3, '0');
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
    public void startMap(int currentMembers, int indexID) throws IOException {
        work.append(MAP_BEGIN);
        addField(indexID);
        addField(currentMembers);
    }

    @Override
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) throws IOException {
        work.append(ARRAY_BEGIN);
        addField(currentMembers);
    }

    @Override
    public void terminateArray() throws IOException {
        work.append(ARRAY_TERMINATOR);
    }

    @Override
    public void terminateMap() throws IOException {
        work.append(ARRAY_TERMINATOR);
    }


    @Override
    public void startObject(BonaPortable obj) throws IOException {
        work.append(OBJECT_BEGIN);
        work.append(obj.get$PQON());
        terminateField();
        addField(obj.get$Revision(), 0);
    }
    
    @Override
    public void addField(BonaPortable obj) throws IOException {
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
}
