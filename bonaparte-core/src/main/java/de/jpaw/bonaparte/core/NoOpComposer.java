package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.util.ByteArray;

/** Represents some stub methods. */
public class NoOpComposer implements MessageComposer<RuntimeException> {
    
    public NoOpComposer() {
    }
    
    @Override
    public void writeNull() {
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
    public void addUnicodeString(String s, int length, boolean allowCtrls) {
    }

    @Override
    public void addField(String s, int length) {
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
    public void addField(Integer n, int length, boolean isSigned) {
    }

    @Override
    public void addField(BigDecimal n, int length, int decimals, boolean isSigned) {
    }

    @Override
    public void addField(UUID n) {
    }

    @Override
    public void addField(ByteArray b, int length) {
    }

    @Override
    public void addField(byte[] b, int length) {
    }

    @Override
    public void addField(Calendar t, boolean hhmmss, int length) {
    }

    @Override
    public void addField(LocalDate t) {
    }

    @Override
    public void addField(LocalDateTime t, boolean hhmmss, int length) {
    }

    @Override
    public void addField(BonaPortable obj) {
    }

    @Override
    public void startObject(BonaPortable obj) {
    }

}
