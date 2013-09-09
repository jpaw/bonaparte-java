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
import java.util.UUID;
import java.util.Calendar;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.util.ByteArray;

/**
 * The MessageComposer interface.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines the methods required for any serialization implementation
 */

public interface MessageComposer<E extends Exception> {

    // serialization methods: structure
    public void writeNull(FieldDefinition di) throws E;
    public void startTransmission() throws E;
    public void startRecord() throws E;
    public void startObject(BonaPortable o) throws E;  // write the name and the revision
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) throws E;
    public void startMap  (int currentMembers, int indexID) throws E;
    public void writeSuperclassSeparator() throws E;  // this is bad. It should be transparent to the classes if the message format contains separators or not.
    public void terminateMap() throws E;
    public void terminateArray() throws E;
    public void terminateRecord() throws E;
    public void terminateTransmission() throws E;
    public void writeRecord(BonaPortable o) throws E;

    // serialization methods: field type specific

    // primitives
    void addField(boolean b) throws E;
    void addField(char c) throws E;
    void addField(double d) throws E;
    void addField(float f) throws E;
    void addField(byte n) throws E;
    void addField(short n) throws E;
    void addField(int n) throws E;
    void addField(long n) throws E;

    void addField(AlphanumericElementaryDataItem di, String s) throws E;    // Ascii, Upper, Lower, Unicode
    void addField(BonaPortable obj) throws E;
    void addField(MiscElementaryDataItem di, UUID n) throws E;
    void addField(BinaryElementaryDataItem di, ByteArray b) throws E;
    void addField(BinaryElementaryDataItem di, byte [] b) throws E;
    void addField(NumericElementaryDataItem di, Integer n) throws E;
    void addField(NumericElementaryDataItem di, BigDecimal n) throws E;
    void addField(TemporalElementaryDataItem di, Calendar t) throws E;
    void addField(TemporalElementaryDataItem di, LocalDate t) throws E;
    void addField(TemporalElementaryDataItem di, LocalDateTime t) throws E;
}
