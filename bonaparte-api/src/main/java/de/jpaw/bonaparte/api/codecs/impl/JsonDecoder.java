package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteArray;

public class JsonDecoder<O extends BonaPortable> implements IMessageDecoder<O, byte []> {
    
    private final Class<O> decoderClass;
    
    public JsonDecoder(Class<O> decoderClass) {
        this.decoderClass = decoderClass;
    }
    
    @Override
    public O decode(byte [] data, ObjectReference di) throws ApplicationException {
        final JsonParser jp = new JsonParser(new String(data, ByteArray.CHARSET_UTF8), false);
        return CastIt.castTo(MapParser.asBonaPortable(jp.parseObject(), di), decoderClass);
    }
}
