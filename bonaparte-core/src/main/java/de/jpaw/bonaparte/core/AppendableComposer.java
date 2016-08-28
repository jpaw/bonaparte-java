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
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.util.DayTime;
import de.jpaw.bonaparte.util.FixASCII;
import de.jpaw.enums.XEnum;
import de.jpaw.json.JsonEscaper;
import de.jpaw.util.Base64;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import jakarta.xml.bind.DatatypeConverter;  // but not in Android, so don't use it!
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

public class AppendableComposer extends AbstractMessageComposer<IOException> implements StringBuilderConstants {
    //private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayComposer.class);
    private final boolean useCache;
    private final Map<BonaCustom,Integer> objectCache;
    private int numberOfObjectsSerialized;
    private int numberOfObjectReuses;
    // variables set by constructor
    private final Appendable work;
    private JsonEscaper jsonEscaper = null;

    public AppendableComposer(Appendable work) {
        this(work, ObjectReuseStrategy.defaultStrategy);
    }

    /** Creates a new ByteArrayComposer, using this classes static default Charset **/
    public AppendableComposer(Appendable work, ObjectReuseStrategy reuseStrategy) {
        switch (reuseStrategy) {
        case BY_CONTENTS:
            this.objectCache = new HashMap<BonaCustom, Integer>(250);
            this.useCache = true;
            break;
        case BY_REFERENCE:
            this.objectCache = new IdentityHashMap<BonaCustom, Integer>(250);
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

    // must be overridden / called if caching / reuse is active!
    public void reset() {
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
        if (useCache)
            objectCache.clear();

    }

    // for statistics
    public int getNumberOfObjectReuses() {
        return numberOfObjectReuses;
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
    protected void writeNull() throws IOException {
        work.append(NULL_FIELD);
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        work.append(NULL_FIELD);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
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
        if (getWriteCRs()) {
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

    private void addCharSub(char c) throws IOException {
        if ((c >= 0) && (c < ' ') && (c != '\t')) {
            work.append(ESCAPE_CHAR);
            work.append((char)(c + '@'));
        } else {
            work.append(c);
        }
    }

    // field type specific output functions

    // character
    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        addCharSub(c);
        terminateField();
    }
    // ascii only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (s != null) {
            if (di.getRestrictToAscii()) {
                work.append(FixASCII.checkAsciiAndFixIfRequired(s, di.getLength(), di.getName()));
            } else {
                for (int i = 0; i < s.length(); ++i) {
                    addCharSub(s.charAt(i));
                }
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        if (n != null) {
            work.append(n.toPlainString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // byte
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        work.append(Byte.toString(n));
        terminateField();
    }
    // short
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        work.append(Short.toString(n));
        terminateField();
    }
    // integer
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        work.append(Integer.toString(n));
        terminateField();
    }

    // int(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        if (n != null) {
            work.append(n.toString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // long
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        work.append(Long.toString(n));
        terminateField();
    }

    // boolean
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        if (b) {
            work.append('1');
        } else {
            work.append('0');
        }
        terminateField();
    }

    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        work.append(Float.toString(f));
        terminateField();
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        work.append(Double.toString(d));
        terminateField();
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null) {
            work.append(n.toString());
            terminateField();
        } else {
            writeNull();
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b != null) {
            ByteBuilder tmp = new ByteBuilder((b.length() * 2) + 4, null);
            b.appendBase64(tmp);
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
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
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
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        if (t != null) {
            work.append(Integer.toString(DayTime.dayAsInt(t)));
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        if (t != null) {
            int [] values = t.getValues(); // 4 values: year, month, day, millis of day
            //int tmpValue = 10000 * t.getYear() + 100 * t.getMonthOfYear() + t.getDayOfMonth();
            work.append(Integer.toString((10000 * values[0]) + (100 * values[1]) + values[2]));
            int length = di.getFractionalSeconds();
            if (length >= 0) {
                // not only day, but also time
                //tmpValue = 10000 * t.getHourOfDay() + 100 * t.getMinuteOfHour() + t.getSecondOfMinute();
                if (length > 0 ? (values[3] != 0) : ((values[3] / 1000) != 0)) {
                    work.append('.');
                    if (di.getHhmmss()) {
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
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        if (t != null) {
            int length = di.getFractionalSeconds();
            int millis = t.getMillisOfDay();
            if (di.getHhmmss()) {
                int tmpValue = millis / 60000; // minutes and hours
                tmpValue = (100 * (tmpValue / 60)) + (tmpValue % 60);
                work.append(Integer.toString((tmpValue * 100) + ((millis % 60000) / 1000)));
            } else {
                work.append(Integer.toString(millis / 1000));
            }
            if (length > 0 && (millis % 1000) != 0) {
                // add milliseconds
                work.append('.');
                int milliSeconds = millis % 1000;
                lpad(Integer.toString(milliSeconds), 3, '0');
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        if (t != null) {
            long millis = t.getMillis();
            work.append(Long.toString(millis / 1000L));
            int length = di.getFractionalSeconds();
            int millisecs = (int)(millis % 1000L);
            if (length > 0 && millisecs != 0) {
                work.append('.');
                lpad(Integer.toString(millisecs), 3, '0');
            }
            terminateField();
        } else {
            writeNull();
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        work.append(MAP_BEGIN);
        addField(StaticMeta.INTERNAL_INTEGER, di.getMapIndexType().ordinal());
        addField(StaticMeta.INTERNAL_INTEGER, currentMembers);
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        work.append(ARRAY_BEGIN);
        addField(StaticMeta.INTERNAL_INTEGER, currentMembers);
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
    public void startObject(ObjectReference di, BonaCustom obj) throws IOException {
        work.append(OBJECT_BEGIN);
        addField(OBJECT_CLASS, obj.ret$PQON());
        addField(REVISION_META, obj.ret$MetaData().getRevision());
    }

    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        work.append(OBJECT_TERMINATOR);
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        if (obj == null) {
            writeNull();
        } else {
            if (useCache) {
                Integer previousIndex = objectCache.get(obj);
                if (previousIndex != null) {
                    // reuse this instance
                    work.append(OBJECT_AGAIN);
                    addField(StaticMeta.INTERNAL_INTEGER, numberOfObjectsSerialized - previousIndex.intValue() - 1);  // 0 is same object as previous, 1 = the one before etc...
                    ++numberOfObjectReuses;
                    return;
                }
                // add the new object to the cache of known objects. This is done despite we are not yet done with the object!
                objectCache.put(obj, Integer.valueOf(numberOfObjectsSerialized++));
                // fall through
            }
            // start a new object
            startObject(di, obj);
            // do all fields (now includes terminator)
            obj.serializeSub(this);
            // terminate the object
            terminateObject(di, obj);
        }
    }

    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) throws IOException {
        if (n == null)
            writeNull(ord);
        else
            addField(ord, n.ordinal());
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws IOException {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }

    // xenum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) throws IOException {
        return false;       // perform conversion by default
    }

    @Override
    public void addField(ObjectReference di, Map<String, Object> obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        // not null, compose a string and write it!
        if (jsonEscaper == null)
            jsonEscaper = new BonaparteJsonEscaper(work);
        jsonEscaper.outputJsonObject(obj);      // the JSON string is known not to contain escape characters and needs no quoting
        terminateField();
    }

    @Override
    public void addField(ObjectReference di, List<Object> obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        // not null, compose a string and write it!
        if (jsonEscaper == null)
            jsonEscaper = new BonaparteJsonEscaper(work);
        jsonEscaper.outputJsonArray(obj);      // the JSON string is known not to contain escape characters and needs no quoting
        terminateField();
    }

    @Override
    public void addField(ObjectReference di, Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        // not null, compose a string and write it!
        if (jsonEscaper == null)
            jsonEscaper = new BonaparteJsonEscaper(work);
        jsonEscaper.outputJsonElement(obj);      // the JSON string is known not to contain escape characters and needs no quoting
        terminateField();
    }
}
