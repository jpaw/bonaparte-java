package de.jpaw.bonaparte.api.codecs.impl;

import java.util.HashMap;
import java.util.Map;

import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;

public class SingleThreadCachingMessageCoderFactory2<D extends BonaPortable, E extends BonaPortable> extends MessageCoderFactory2<D, E> {
    private final Map<String,IMessageDecoder<D, byte []>> decoders = new HashMap<String,IMessageDecoder<D, byte []>>(10);
    private final Map<String,IMessageEncoder<E, byte []>> encoders = new HashMap<String,IMessageEncoder<E, byte []>>(10);

    public SingleThreadCachingMessageCoderFactory2(Class<D> decoderClass, Class<E> encoderClass) {
        super(decoderClass, encoderClass);
    }

    @Override
    public IMessageEncoder<E, byte []> getEncoderInstance(String mimeType) {
        IMessageEncoder<E, byte []> encoder = encoders.get(mimeType);
        if (encoder != null)
            return encoder;
        encoder = super.getEncoderInstance(mimeType);
        if (encoder != null)
            encoders.put(mimeType, encoder);
        return encoder;
    }

    @Override
    public IMessageDecoder<D, byte []> getDecoderInstance(String mimeType) {
        IMessageDecoder<D, byte []> decoder = decoders.get(mimeType);
        if (decoder != null)
            return decoder;
        decoder = super.getDecoderInstance(mimeType);
        if (decoder != null)
            decoders.put(mimeType, decoder);
        return decoder;
    }
}
