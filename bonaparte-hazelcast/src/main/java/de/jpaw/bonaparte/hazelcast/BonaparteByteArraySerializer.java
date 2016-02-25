package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;

import com.hazelcast.nio.serialization.ByteArraySerializer;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;

public class BonaparteByteArraySerializer extends BonaparteSerializer implements ByteArraySerializer<BonaPortable> {
    private final boolean recommendIds;

    public BonaparteByteArraySerializer() {
        this.recommendIds = false;
    }
    public BonaparteByteArraySerializer(boolean recommendIds) {
        this.recommendIds = recommendIds;
    }

    @Override
    public byte[] write(BonaPortable object) throws IOException {
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(500, recommendIds);
        cbac.addField(StaticMeta.OUTER_BONAPORTABLE, object);
        return cbac.getBuilder().getBytes();
    }

    @Override
    public BonaPortable read(byte[] buffer) throws IOException {
        try {
            return CompactByteArrayParser.unmarshal(buffer, StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
        } catch (MessageParserException e) {
            throw new IOException("Parse exception: " + e.getMessage(), e);
        }
    }
}
