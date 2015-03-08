package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.PortableWriter;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.NoOpComposer;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.enums.BonaNonTokenizableEnum;
import de.jpaw.bonaparte.enums.BonaTokenizableEnum;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.XEnum;

/** The strategy of the HazelcastPortableComposer is to write selected fields of the primary object using the portable format (with name), followed
 * by a dump of the whole object tree in compact format as raw data appendix.
 *
 */
public class HazelcastPortableComposer extends NoOpComposer<IOException> implements MessageComposer<IOException> {
    protected final PortableWriter w;

    /** Writes the whole object (tree) to the raw stream of the PortableWriter, as appendix. */
    private static void serializeRaw(BonaPortable obj, PortableWriter _out) throws IOException {
        ObjectDataOutput out = _out.getRawDataOutput();  // this is a superclass of DatOutput, therefore we can use the CompactComposer on it
        CompactComposer composer = new CompactComposer(out, true);
        composer.addField(StaticMeta.OUTER_BONAPORTABLE, obj);
    }

    // entry called from generated objects: all fields of primitive types, their wrappers, enums, and the UUID will be added as named fields.
    public static void serialize(BonaPortable obj, PortableWriter _out) throws IOException {
        // first, output all fields of the core class.
        obj.serializeSub(new HazelcastPortableComposer(_out));
        // second, write the raw data
        serializeRaw(obj, _out);
    }

    // writes only the named fields, and then dumps the whole object in compact format
    public static void serialize(BonaPortable obj, PortableWriter _out, List<String> fieldnames) throws IOException {
        FoldingComposer.writeFieldsToDelegate(new HazelcastPortableComposer(_out), obj, fieldnames);

    }

    public HazelcastPortableComposer(PortableWriter w) {
        this.w = w;
    }

    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        w.writeBoolean(di.getName(), b);
    }

    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
        w.writeChar(di.getName(), c);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        w.writeDouble(di.getName(), d);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        w.writeFloat(di.getName(), f);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        w.writeByte(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        w.writeShort(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        w.writeInt(di.getName(), n);
    }

    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        w.writeLong(di.getName(), n);
    }

    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (s != null)
            w.writeUTF(di.getName(), s);
    }

    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null)
            w.writeUTF(di.getName(), n.toString());
    }

    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, BonaNonTokenizableEnum n) throws IOException {
        if (n != null)
            w.writeInt(di.getName(), n.ordinal());
    }

    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, BonaTokenizableEnum n) throws IOException {
        if (n != null)
            w.writeUTF(di.getName(), n.getToken());
    }

    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
        if (n != null)
            w.writeUTF(di.getName(), n.getToken());
    }

    @Override
    public boolean addExternal(ObjectReference di, Object obj) throws IOException {
        return false;
    }
}
