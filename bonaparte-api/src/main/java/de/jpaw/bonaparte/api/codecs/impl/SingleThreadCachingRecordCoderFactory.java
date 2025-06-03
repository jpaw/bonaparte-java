package de.jpaw.bonaparte.api.codecs.impl;

import java.util.HashMap;
import java.util.Map;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;

public class SingleThreadCachingRecordCoderFactory extends RecordCoderFactory {
    private final Map<String,IMessageDecoder<BonaPortable, byte []>> decoders = new HashMap<String,IMessageDecoder<BonaPortable, byte []>>(12);
    private final Map<String,IMessageEncoder<BonaPortable, byte []>> encoders = new HashMap<String,IMessageEncoder<BonaPortable, byte []>>(12);

    @Override
    public IMessageEncoder<BonaPortable, byte []> getEncoderInstance(String mimeType) {
        IMessageEncoder<BonaPortable, byte []> encoder = encoders.get(mimeType);
        if (encoder != null)
            return encoder;
        encoder = super.getEncoderInstance(mimeType);
        if (encoder != null)
            encoders.put(mimeType, encoder);
        return encoder;
    }

    @Override
    public IMessageDecoder<BonaPortable, byte []> getDecoderInstance(String mimeType) {
        IMessageDecoder<BonaPortable, byte []> decoder = decoders.get(mimeType);
        if (decoder != null)
            return decoder;
        decoder = super.getDecoderInstance(mimeType);
        if (decoder != null)
            decoders.put(mimeType, decoder);
        return decoder;
    }
}
