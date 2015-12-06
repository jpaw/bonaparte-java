package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class BonaparteRecordDecoder<O extends BonaPortable> implements IMessageDecoder<O, byte []> {
    
    @Override
    public O decode(byte [] data, ObjectReference di) throws MessageParserException {
        final ByteArrayParser cbap = new ByteArrayParser(data, 0, -1);
        return (O) cbap.readRecord();
    }
}
