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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.LongFunction;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.AlphanumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericEnumSetDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDataItem;
import de.jpaw.enums.AbstractXEnumBase;
import de.jpaw.enums.XEnumFactory;
import de.jpaw.fixedpoint.FixedPointBase;
import de.jpaw.util.ByteArray;

/**
 * The MessageParser interface.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines the methods required for any parser implementation
 */

public interface MessageParser<E extends Exception> extends ExceptionConverter<E> {
    public static final String GENERIC_RECORD = "RECORD";   // field name to be used when parsing top level record

    // unmarshaller methods: field type specific
    public BigDecimal readBigDecimal(NumericElementaryDataItem di) throws E;
    public Character  readCharacter (MiscElementaryDataItem di) throws E;
    public UUID       readUUID      (MiscElementaryDataItem di) throws E;
    public Boolean    readBoolean   (MiscElementaryDataItem di) throws E;
    public Double     readDouble    (BasicNumericElementaryDataItem di) throws E;
    public Float      readFloat     (BasicNumericElementaryDataItem di) throws E;
    public Long       readLong      (BasicNumericElementaryDataItem di) throws E;
    public Integer    readInteger   (BasicNumericElementaryDataItem di) throws E;
    public Short      readShort     (BasicNumericElementaryDataItem di) throws E;
    public Byte       readByte      (BasicNumericElementaryDataItem di) throws E;
    public BigInteger readBigInteger(BasicNumericElementaryDataItem di) throws E;
    public <F extends FixedPointBase<F>> F readFixedPoint(BasicNumericElementaryDataItem di, LongFunction<F> factory) throws E;
    public String     readAscii     (AlphanumericElementaryDataItem di) throws E;
    public String     readString    (AlphanumericElementaryDataItem di) throws E;
    public ByteArray  readByteArray (BinaryElementaryDataItem di) throws E;
    public byte []    readRaw       (BinaryElementaryDataItem di) throws E;
    public Instant    readInstant   (TemporalElementaryDataItem di) throws E;
    public LocalDate  readDay       (TemporalElementaryDataItem di) throws E;
    public LocalTime  readTime      (TemporalElementaryDataItem di) throws E;
    public LocalDateTime readDayTime(TemporalElementaryDataItem di) throws E;
    public <R extends BonaPortable> R   readObject (ObjectReference di, Class<R> type) throws E; // parser factory
    public Map<String, Object>          readJson   (ObjectReference di) throws E;
    public List<Object>                 readArray  (ObjectReference di) throws E;
    public Object                       readElement(ObjectReference di) throws E;
    // composite methods
    public int parseMapStart        (FieldDefinition di) throws E;
    public int parseArrayStart      (FieldDefinition di, int sizeOfElement) throws E;
    public void parseArrayEnd() throws E;
    public BonaPortable       readRecord()       throws E;
    public List<BonaPortable> readTransmission() throws E;
    // helper functions
    public void setClassName(String newClassName); // returns the previously active class name
    public void eatParentSeparator() throws E;  // restores the previous class name
    public Integer                              readEnum(EnumDataItem edi, BasicNumericElementaryDataItem di) throws E;
    public String                               readEnum(EnumDataItem edi, AlphanumericElementaryDataItem di) throws E;
    public <T extends AbstractXEnumBase<T>> T   readXEnum(XEnumDataItem di, XEnumFactory<T> factory) throws E;

    // separate entries for enumsets allow to override them without any impact to regular alphanumeric / numeric methods
    // these methods are supposed to return the token strings / bitmaps of the enum sets
    default String    readString4Xenumset    (XEnumSetDataItem            di) throws E { return readString(di); }
    default String    readString4EnumSet     (AlphanumericEnumSetDataItem di) throws E { return readString(di); }
    default Long      readLong4EnumSet       (NumericEnumSetDataItem      di) throws E { return readLong(di); }
    default Integer   readInteger4EnumSet    (NumericEnumSetDataItem      di) throws E { return readInteger(di); }
    default Short     readShort4EnumSet      (NumericEnumSetDataItem      di) throws E { return readShort(di); }
    default Byte      readByte4EnumSet       (NumericEnumSetDataItem      di) throws E { return readByte(di); }

    // add the primitive types
    public char       readPrimitiveCharacter (MiscElementaryDataItem      di) throws E;
    public boolean    readPrimitiveBoolean   (MiscElementaryDataItem      di) throws E;
    public double     readPrimitiveDouble    (BasicNumericElementaryDataItem di) throws E;
    public float      readPrimitiveFloat     (BasicNumericElementaryDataItem di) throws E;
    public long       readPrimitiveLong      (BasicNumericElementaryDataItem di) throws E;
    public int        readPrimitiveInteger   (BasicNumericElementaryDataItem di) throws E;
    public short      readPrimitiveShort     (BasicNumericElementaryDataItem di) throws E;
    public byte       readPrimitiveByte      (BasicNumericElementaryDataItem di) throws E;
}
