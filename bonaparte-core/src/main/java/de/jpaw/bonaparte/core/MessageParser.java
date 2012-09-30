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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import de.jpaw.util.ByteArray;
import de.jpaw.util.EnumException;

/**
 * The MessageParser interface.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Defines the methods required for any parser implementation
 */

public interface MessageParser<E extends Exception> {
    // unmarshaller methods: field type specific
    public BigDecimal readBigDecimal(boolean allowNull, int length, int decimals, boolean isSigned) throws E;
    public Character  readCharacter (boolean allowNull) throws E;
    public UUID    readUUID     (boolean allowNull) throws E;
    public Boolean readBoolean  (boolean allowNull) throws E;
    public Double  readDouble   (boolean allowNull, boolean isSigned) throws E;
    public Float   readFloat    (boolean allowNull, boolean isSigned) throws E;
    public Long    readLong     (boolean allowNull, boolean isSigned) throws E;
    public Integer readInteger  (boolean allowNull, boolean isSigned) throws E;
    public Short   readShort    (boolean allowNull, boolean isSigned) throws E;
    public Byte    readByte     (boolean allowNull, boolean isSigned) throws E;
    public Integer readNumber   (boolean allowNull, int length, boolean isSigned)   throws E;
    public String  readAscii    (boolean allowNull, int length, boolean doTrim, boolean doTruncate) throws E;
    public String  readString   (boolean allowNull, int length, boolean doTrim, boolean doTruncate, boolean allowCtrls, boolean allowUnicode) throws E;
    public ByteArray readByteArray(boolean allowNull, int length) throws E;
    public byte [] readRaw      (boolean allowNull, int length) throws E;
    public GregorianCalendar readGregorianCalendar(boolean allowNull, boolean hhmmss, int fractionalDigits) throws E;
    public LocalDate readDay    (boolean allowNull) throws E;
    public LocalDateTime readDayTime(boolean allowNull, boolean hhmmss, int length) throws E;
    // composite methods
    public BonaPortable readRecord() throws E;
    public int parseArrayStart(int max, int sizeOfElement) throws E;
    public void parseArrayEnd()     throws E;
    public BonaPortable readObject(Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws E; // parser factory
    public List<BonaPortable> readTransmission()        throws E;
    // helper functions
    public void setClassName(String newClassName); // returns the previously active class name
    public void eatParentSeparator() throws E;  // restores the previous class name
    public E enumExceptionConverter(EnumException e);  // convert e to an exception of appropriate type. Also enrich it with current parser status
    // upload of current class to be parsed: now all done locally within the parser
    // public String setCurrentClass(String classname);
    
    // methods from common settings
    // omit, these are only valid for the ASCII / UTF format. Set them on the Composer object directly, or create an "ASCIIComposer" interface
    //public boolean doWriteCRs();
    //public void setWriteCRs(boolean writeCRs);
    //public Charset getCharset();
    //public void setCharset(Charset charset);
}
