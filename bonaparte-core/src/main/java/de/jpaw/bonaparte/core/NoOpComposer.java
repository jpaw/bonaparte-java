package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.enums.TokenizableEnum;
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
    public void startArray(int currentMembers, int maxMembers, int sizeOfElement) {
    }

    @Override
    public void startMap(int currentMembers, int indexID) {
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
    public void addField(boolean b) {
    }

    @Override
    public void addField(char c) {
    }

    @Override
    public void addField(double d) {
    }

    @Override
    public void addField(float f) {
    }

    @Override
    public void addField(byte n) {
    }

    @Override
    public void addField(short n) {
    }

    @Override
    public void addField(int n) {
    }

    @Override
    public void addField(long n) {
    }

    @Override
    public void addField(NumericElementaryDataItem di, Integer n) {
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
    public void addField(TemporalElementaryDataItem di, Calendar t) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
    }

    @Override
    public void addField(BonaPortable obj) {
    }

    @Override
    public void startObject(BonaPortable obj) {
    }

    @Override
    public void addEnum(EnumDataItem di, NumericElementaryDataItem ord, Enum<?> n) throws RuntimeException {
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws RuntimeException {
    }

}
