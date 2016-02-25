package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class CompactBonaparteDecoder<O extends BonaPortable> implements IMessageDecoder<O, byte []> {

    private final Class<O> decoderClass;

    public CompactBonaparteDecoder(Class<O> decoderClass) {
        this.decoderClass = decoderClass;
    }

    @Override
    public O decode(byte [] data, ObjectReference di) throws MessageParserException {
        final CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, -1);
        return cbap.readObject(di, decoderClass);
    }
}
