package de.jpaw.bonaparte.validation;

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/**
 * The BonaValidator interface.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Defines the methods which are called in order to execute field validation.
 */

public interface BonaValidator {
    // checks on composite structures
    public void checkArray(FieldDefinition di, int currentMembers) throws ObjectValidationException;
    public void checkList(FieldDefinition di, List<?> l) throws ObjectValidationException;
    public void checkSet(FieldDefinition di, Set<?> s) throws ObjectValidationException;
    public void checkMap(FieldDefinition di, Map<?,?> m) throws ObjectValidationException;

    // primitives
    void check(MiscElementaryDataItem di, boolean b) throws ObjectValidationException;
    void check(MiscElementaryDataItem di, char c) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, double d) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, float f) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, byte n) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, short n) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, int n) throws ObjectValidationException;
    void check(BasicNumericElementaryDataItem di, long n) throws ObjectValidationException;

    void check(BasicNumericElementaryDataItem di, BigInteger n) throws ObjectValidationException;
    void check(NumericElementaryDataItem di, BigDecimal n) throws ObjectValidationException;
    void check(MiscElementaryDataItem di, UUID n) throws ObjectValidationException;
    void check(BinaryElementaryDataItem di, ByteArray b) throws ObjectValidationException;
    void check(BinaryElementaryDataItem di, byte[] b) throws ObjectValidationException;

    void check(AlphanumericElementaryDataItem di, String s, Pattern pattern) throws ObjectValidationException; // Ascii, Upper, Lower, Unicode
    void check(ObjectReference di, BonaPortable obj) throws ObjectValidationException;

    void check(TemporalElementaryDataItem di, Instant t) throws ObjectValidationException;
    void check(TemporalElementaryDataItem di, LocalDate t) throws ObjectValidationException;
    void check(TemporalElementaryDataItem di, LocalTime t) throws ObjectValidationException;
    void check(TemporalElementaryDataItem di, LocalDateTime t) throws ObjectValidationException;

    // Enums
    void check(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws ObjectValidationException;
    void check(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws ObjectValidationException;
    void check(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws ObjectValidationException;
}
