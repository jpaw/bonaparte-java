package de.jpaw.bonaparte8.vertx3;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.core.StaticMeta;

public class CompactMessageCodec implements MessageCodec<BonaPortable, BonaPortable> {
    public static final String COMPACT_MESSAGE_CODEC_ID = "cb";

    @Override
    public void encodeToWire(Buffer buffer, BonaPortable obj) {
        final CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeObject(obj);
        buffer.setBytes(0, cbac.getBuffer(), 0, cbac.getLength());
    }

    @Override
    public BonaPortable decodeFromWire(int pos, Buffer buffer) {
        final byte [] buff = buffer.getBytes();
        CompactByteArrayParser cbap = new CompactByteArrayParser(buff, pos, buff.length);
        try {
            return cbap.readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BonaPortable transform(BonaPortable s) {
        if (s.was$Frozen())
            return s;       // immutable
        try {
//            return s.ret$MutableClone(true, false);
            return s.ret$FrozenClone();
        } catch (ObjectValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return COMPACT_MESSAGE_CODEC_ID;
    }

    @Override
    public byte systemCodecID() {
        return (byte)-1;
    }
}
