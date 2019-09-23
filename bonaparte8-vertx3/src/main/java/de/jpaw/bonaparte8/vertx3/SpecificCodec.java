package de.jpaw.bonaparte8.vertx3;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.core.StaticMeta;

public class SpecificCodec<T extends BonaPortable> implements MessageCodec<T, T> {

    private final Class<T> specific;
    private final String id;

    public SpecificCodec(Class<T> specific, String id) {
        this.specific = specific;
        this.id = id;
    }

    @Override
    public void encodeToWire(Buffer buffer, T obj) {
        final CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeObject(obj);
        buffer.setBytes(0, cbac.getBuffer(), 0, cbac.getLength());
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        final byte [] buff = buffer.getBytes();
        CompactByteArrayParser cbap = new CompactByteArrayParser(buff, pos, buff.length);
        try {
            return cbap.readObject(StaticMeta.OUTER_BONAPORTABLE, specific);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transform(T s) {
        if (s.was$Frozen())
            return s;       // immutable
        try {
//            return s.ret$MutableClone(true, false);
            return (T) s.ret$FrozenClone();
        } catch (ObjectValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return id;
    }

    @Override
    public byte systemCodecID() {
        return (byte)-1;
    }



}
