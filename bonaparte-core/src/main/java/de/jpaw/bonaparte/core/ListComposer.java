package de.jpaw.bonaparte.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
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
    public void writeNull(FieldDefinition di) {
        storage.add(null);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) {
        storage.add(null);
    }


    @Override
    public void writeRecord(BonaPortable o) {
        startRecord();  // noop in the base implementation
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) {
        storage.add(s);
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) {
        storage.add(Boolean.valueOf(b));
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) {
        storage.add(Character.valueOf(c));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) {
        storage.add(Double.valueOf(d));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) {
        storage.add(Float.valueOf(f));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) {
        storage.add(Byte.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) {
        storage.add(Short.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) {
        storage.add(Integer.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) {
        storage.add(Long.valueOf(n));
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, Integer n) {
        storage.add(n);
    }

    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) {
        storage.add(n);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) {
        storage.add(n);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) {
        storage.add(b);
    }

    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) {
        if (b == null)
            writeNull(di);
        storage.add(doDeepCopies ? Arrays.copyOf(b, b.length) : b);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) {
        if (t == null)
            writeNull(di);
        storage.add(doDeepCopies ? (Calendar)t.clone() : t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) {
        storage.add(t);
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) {
        storage.add(t);
    }

    @Override
    public void addField(ObjectReference di, BonaPortable obj) {
        if (obj == null) {
            writeNull(null);
        } else {
            startObject(di, obj);
            obj.serializeSub(this);
        }
    }
}
