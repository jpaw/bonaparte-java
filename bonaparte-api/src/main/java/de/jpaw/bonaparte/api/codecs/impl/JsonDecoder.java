package de.jpaw.bonaparte.api.codecs.impl;

import java.nio.charset.StandardCharsets;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;

public class JsonDecoder<O extends BonaPortable> implements IMessageDecoder<O, byte []> {
    
    private final Class<O> decoderClass;
    
    public JsonDecoder(Class<O> decoderClass) {
        this.decoderClass = decoderClass;
    }
    
    @Override
    public O decode(byte [] data, ObjectReference di) throws ApplicationException {
        final JsonParser jp = new JsonParser(new String(data, StandardCharsets.UTF_8), false);
        return CastIt.castTo(MapParser.asBonaPortable(jp.parseObject(), di), decoderClass);
    }
}
