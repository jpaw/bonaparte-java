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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

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
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/**
 * The ExternalizableComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines some constants and static utility methods for the Externalizable interface.
 *          About null checking, the principle is that boxed primitive types must be null-checked and possibly unboxed in
 *          the class itself (the caller), but any object which does not exist in primitive form has its null check here.
 */

public class ExternalizableComposer extends AbstractMessageComposer<IOException> implements ExternalizableConstants {

    private final ObjectOutput out;

    public ExternalizableComposer(ObjectOutput out) {
        this.out = out;
    }

    // entry called from generated objects: (Object header has been written already by internal methods (and unfortunately in some different fashion...))
    public static void serialize(BonaCustom obj, ObjectOutput _out) throws IOException {
        MessageComposer<IOException> _w = new ExternalizableComposer(_out);
        obj.serializeSub(_w);
        _w.terminateObject(StaticMeta.OUTER_BONAPORTABLE, obj);
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        out.writeByte(NULL_FIELD);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
        out.writeByte(NULL_FIELD);
    }

    /**
     * Primitives
     * */

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        out.writeByte(BINARY_DOUBLE);
        out.writeDouble(d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        out.writeByte(BINARY_FLOAT);
        out.writeFloat(f);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n == null) {
            out.writeByte(NULL_FIELD);
        } else {
            out.writeByte(TEXT);
            out.writeUTF(n.toString());
        }
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (s == null) {
            out.writeByte(NULL_FIELD);
        } else {
            out.writeByte(TEXT);
            out.writeUTF(s);
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b == null) {
            out.writeByte(NULL_FIELD);
        } else {
            out.writeByte(BINARY);
            b.writeExternal(out);
        }
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b == null) {
            out.writeByte(NULL_FIELD);
        } else {
            out.writeByte(BINARY);
            ByteArray.writeBytes(out, b, 0, b.length);
        }
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        out.writeByte(TEXT);
        out.writeUTF(String.valueOf(c));
    }
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        out.writeByte(b ? INT_ONE : INT_ZERO);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        if (n == null) {
            out.writeByte(NULL_FIELD);
        } else {
            int scale = n.scale();
            if ((scale < 0) || (scale > 18)) {
                throw new IOException("cannot convert BigDecimal with negative scale or scale > 18: " + scale);
            }
            long fraction = n.unscaledValue().longValue() % powersOfTen[scale];
            if (fraction != 0) {
                out.writeByte(FRAC_SCALE_0 + scale);
                writeVarLong(fraction);
            }
            writeVarLong(n.longValue());
        }
    }

    // the following method will be used for byte, short, int
    private void writeVarInt(int i) throws IOException {
        if ((i >= -1) && (i <= 16)) {
            out.writeByte(i + INT_ZERO);
        } else if ((i >= -128) && (i <= 127)) {
            out.writeByte(INT_ONEBYTE);
            out.writeByte(i);
        } else if ((i >= -32768) && (i <= 32767)) {
            out.writeByte(INT_TWOBYTES);
            out.writeShort(i);
        } else {
            out.writeByte(INT_FOURBYTES);
            out.writeInt(i);
        }
    }
    // the following method will be used for byte, short, int
    @SuppressWarnings("cast")
    private void writeVarLong(long l) throws IOException {
        if ((long)(int)l == l) {
            writeVarInt((int)l);
        } else {
            out.writeByte(INT_EIGHTBYTES);
            out.writeLong(l);
        }
    }


    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        if (t == null) {
            out.writeByte(NULL_FIELD);
        } else {
            int [] values = t.getValues();   // 3 values: year, month, day
            writeVarInt((10000 * values[0]) + (100 * values[1]) + values[2]);
        }
    }
    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        if (t == null) {
            out.writeByte(NULL_FIELD);
        } else {
            int [] values = t.getValues();   // 4 values: year, month, day, milliseconds
            if (values[3] != 0) {
                // fractional part first...
                out.writeByte(FRAC_SCALE_0 + 9);
                if (di.getHhmmss()) {
                    // convert milliseconds to hhmmssfff format
                    int tmp = values[3] / 60000; // number of minutes
                    tmp = ((tmp / 60) * 100) + (tmp % 60);
                    writeVarInt((tmp * 100000) + (values[3] % 60000));
                } else {
                    writeVarInt(values[3]);
                }
            }
            // then integral part
            writeVarInt((10000 * values[0]) + (100 * values[1]) + values[2]);
        }
    }
    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) throws IOException {
        if (t == null) {
            out.writeByte(NULL_FIELD);
        } else {
            int millis = t.getMillisOfDay();
            out.writeByte(FRAC_SCALE_0 + 9);
            if (di.getHhmmss()) {
                // convert milliseconds to hhmmssfff format
                int tmp = millis / 60000; // number of minutes
                tmp = ((tmp / 60) * 100) + (tmp % 60);
                writeVarInt((tmp * 100000) + (millis % 60000));
            } else {
                writeVarInt(millis);
            }
        }
    }
    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) throws IOException {
        if (t == null) {
            out.writeByte(NULL_FIELD);
        } else {
            long millis = t.getMillis();
            writeVarLong(millis);
        }
    }

    static public void writeObject(ObjectOutput out, BonaCustom obj) throws IOException {
        if (obj == null) {
            out.writeByte(NULL_FIELD);
        } else {
            // do not rely on Java logic, we know the object is BonaCustom and call the externalizer interface directly
            // do we really? (At the moment it's optional)
            out.writeByte(OBJECT_BEGIN);
            if (nestedObjectsInternally) {
                out.writeUTF(obj.ret$PQON());
                out.writeLong(obj.ret$MetaData().getSerialUID());
                ((Externalizable)obj).writeExternal(out);  // TODO: obj.deserialize(this);
            } else {
                out.writeObject(obj);   // so fall back to normal behaviour!
            }
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        out.writeByte(MAP_BEGIN);
        writeVarInt(di.getMapIndexType().ordinal());
        writeVarInt(currentMembers);
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        out.writeByte(ARRAY_BEGIN);
        writeVarInt(currentMembers);
    }




    /* the following do not really apply at object serialization level, but are provided
     * in order to be able to extend this composer to other tasks
     * @see de.jpaw.bonaparte.core.MessageComposer#startTransmission()
     */

    @Override
    public void startTransmission() throws IOException {
        out.write(TRANSMISSION_BEGIN);
        writeNull(null);    // blank version number
    }

//    public void startObject(String name, String version) throws IOException {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
        out.writeByte(PARENT_SEPARATOR);
    }

    @Override
    public void terminateArray() throws IOException {
        out.writeByte(ARRAY_TERMINATOR);
    }

    @Override
    public void terminateMap() throws IOException {
        out.writeByte(ARRAY_TERMINATOR);
    }


    @Override
    public void terminateRecord() throws IOException {
        out.write(RECORD_TERMINATOR);
    }

    @Override
    public void terminateTransmission() throws IOException {
        out.write(TRANSMISSION_TERMINATOR);
        out.write(TRANSMISSION_TERMINATOR2);
    }

    @Override
    public void startRecord() throws IOException {
        out.write(RECORD_BEGIN);
        writeNull(null);  // blank version number
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        writeVarInt(n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        writeVarInt(n);
    }



    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        writeVarLong(n);

    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        writeVarInt(n);

    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, BigInteger n) throws IOException {
        if (n == null) {
            out.writeByte(NULL_FIELD);
        } else {
            // write it as a byte array: an improved version would "steal" the existing internal byte Array using reflection, instead of copying the stuff
            out.writeByte(BINARY);
            byte [] tmp = n.toByteArray();
            ByteArray.writeBytes(out, tmp, 0, tmp.length);
        }
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) throws IOException {
    }
    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) throws IOException {
        out.writeByte(OBJECT_TERMINATOR);
    }


    @Override
    public void addField(ObjectReference di, BonaCustom obj) throws IOException {
        if (obj == null) {
            out.writeByte(NULL_FIELD);
        } else {
            // do not rely on Java logic, we know the object is BonaCustom and call the externalizer interface directly
            // do we really? (At the moment it's optional)
            startObject(di, obj);
            out.writeByte(OBJECT_BEGIN);  // this logically belongs to the lines below, do not split here!
            if (nestedObjectsInternally) {
                out.writeUTF(obj.ret$PQON());
                addField(REVISION_META, obj.ret$MetaData().getRevision());
                obj.serializeSub(this);
            } else {
                out.writeObject(obj);   // so fall back to normal behaviour!
            }
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
        if (obj == null)
            writeNull(di);
        else {
            out.writeByte(TEXT);
            out.writeUTF(BonaparteJsonEscaper.asJson(obj));
        }
    }

    @Override
    public void addField(ObjectReference di, Object obj) throws IOException {
        if (obj == null)
            writeNull(di);
        else {
            out.writeByte(TEXT);
            out.writeUTF(BonaparteJsonEscaper.asJson(obj));
        }
    }
}
