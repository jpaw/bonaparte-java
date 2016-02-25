package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.CompactParser;
import de.jpaw.bonaparte.core.StaticMeta;

public class BonaparteStreamSerializer extends BonaparteSerializer implements StreamSerializer<BonaPortable> {
    private final boolean recommendIds;

    public BonaparteStreamSerializer() {
        this.recommendIds = false;
    }
    public BonaparteStreamSerializer(boolean recommendIds) {
        this.recommendIds = recommendIds;
    }


    @Override
    public void write(ObjectDataOutput out, BonaPortable object) throws IOException {
        CompactComposer cbac = new CompactComposer(out, recommendIds);
        cbac.addField(StaticMeta.OUTER_BONAPORTABLE, object);
    }

    @Override
    public BonaPortable read(ObjectDataInput in) throws IOException {
        CompactParser p = new CompactParser(in);
        return p.readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }
}
