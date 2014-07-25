package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

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
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** Represents some stub methods. */
public class NoOpComposer implements MessageComposer<RuntimeException> {
    
    public NoOpComposer() {
    }
    
    @Override
    public void writeNull(FieldDefinition di) {
    }
    
    @Override
    public void writeNullCollection(FieldDefinition di) {
    }

    @Override
    public void startTransmission() {
    }

    @Override
    public void startRecord() {
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) {
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) {
    }

    @Override
    public void writeSuperclassSeparator() {
    }

    @Override
    public void terminateMap() {
    }

    @Override
    public void terminateArray() {
    }

    @Override
    public void terminateRecord() {
    }
    
    @Override
    public void terminateTransmission() {
    }

    @Override
    public void writeRecord(BonaPortable o) {
    }
    
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, Integer n) {
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalTime t) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Instant t) {
    }

    @Override
    public void addField(ObjectReference di, BonaPortable obj) {
    }

    @Override
    public void startObject(ObjectReference di, BonaPortable obj) {
    }

    @Override
    public void terminateObject(ObjectReference di, BonaPortable obj) {
    }


    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws RuntimeException {
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws RuntimeException {
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws RuntimeException {
    }

}
