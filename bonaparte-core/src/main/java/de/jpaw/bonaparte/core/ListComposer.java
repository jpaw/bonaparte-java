package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.util.ByteArray;

/** Represents composer which does not serialize, but instead appends all objects into a list.
 * Only the clear() and add() methods of the List interface are used by this implementation.
 * This implementation is not ideal, since it may unbox/rebox objects of the BonaPortables.
 * To improve it, the BonaPortable interface would need to be changed. */
public class ListComposer extends NoOpComposer implements MessageComposer<RuntimeException> {
    final List<Object> storage;
    final boolean doDeepCopies;
    
    public ListComposer(final List<Object> storage, boolean doDeepCopies) {
        this.storage = storage;
        this.doDeepCopies = doDeepCopies;
    }
    
    public List<Object> getStorage() {
        return storage;
    }
    public boolean getDoDeepCopies() {
        return doDeepCopies;
    }
    
    public void reset() {
        storage.clear();
    }

    @Override
    public void writeNull() {
        storage.add(null);
    }


    @Override
    public void writeRecord(BonaPortable o) {
        startRecord();  // noop in the base implementation
        addField(o);
    }

    @Override
    public void addUnicodeString(String s, int length, boolean allowCtrls) {
        storage.add(s);
    }

    @Override
    public void addField(String s, int length) {
        storage.add(s);
    }

    @Override
    public void addField(boolean b) {
        storage.add(Boolean.valueOf(b));
    }

    @Override
    public void addField(char c) {
        storage.add(Character.valueOf(c));
    }

    @Override
    public void addField(double d) {
        storage.add(Double.valueOf(d));
    }

    @Override
    public void addField(float f) {
        storage.add(Float.valueOf(f));
    }

    @Override
    public void addField(byte n) {
        storage.add(Byte.valueOf(n));
    }

    @Override
    public void addField(short n) {
        storage.add(Short.valueOf(n));
    }

    @Override
    public void addField(int n) {
        storage.add(Integer.valueOf(n));
    }

    @Override
    public void addField(long n) {
        storage.add(Long.valueOf(n));
    }

    @Override
    public void addField(Integer n, int length, boolean isSigned) {
        storage.add(Long.valueOf(n));
    }

    @Override
    public void addField(BigDecimal n, int length, int decimals, boolean isSigned) {
        storage.add(n);
    }

    @Override
    public void addField(UUID n) {
        storage.add(n);
    }

    @Override
    public void addField(ByteArray b, int length) {
        storage.add(b);
    }

    @Override
    public void addField(byte[] b, int length) {
        if (b == null)
            writeNull();
        storage.add(doDeepCopies ? Arrays.copyOf(b, b.length) : b);
    }

    @Override
    public void addField(Calendar t, boolean hhmmss, int length) {
        if (t == null)
            writeNull();
        storage.add(doDeepCopies ? (Calendar)t.clone() : t);
    }

    @Override
    public void addField(LocalDate t) {
        storage.add(t);
    }

    @Override
    public void addField(LocalDateTime t, boolean hhmmss, int length) {
        storage.add(t);
    }

    @Override
    public void addField(BonaPortable obj) {
        if (obj == null) {
            writeNull();
        } else {
            startObject(obj);
            obj.serializeSub(this);
        }
    }
}
