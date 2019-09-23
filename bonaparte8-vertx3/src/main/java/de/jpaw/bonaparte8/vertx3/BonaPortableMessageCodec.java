package de.jpaw.bonaparte8.vertx3;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectValidationException;

public class BonaPortableMessageCodec implements MessageCodec<BonaPortable, BonaPortable> {
    public static final String BONAPORTABLE_MESSAGE_CODEC_ID = "bon";

    @Override
    public void encodeToWire(Buffer buffer, BonaPortable s) {
        final ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(s);
        buffer.setBytes(0, bac.getBuffer(), 0, bac.getLength());
    }

    @Override
    public BonaPortable decodeFromWire(int pos, Buffer buffer) {
        final byte [] buff = buffer.getBytes();
        ByteArrayParser bap = new ByteArrayParser(buff, pos, buff.length);
        try {
            return bap.readRecord();
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
        return BONAPORTABLE_MESSAGE_CODEC_ID;
    }

    @Override
    public byte systemCodecID() {
        return (byte)-1;
    }
}
