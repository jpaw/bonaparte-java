package de.jpaw.bonaparte.api.codecs.impl;

import java.util.HashMap;
import java.util.Map;

import de.jpaw.bonaparte.api.codecs.IMessageCoderFactory;
import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MimeTypes;


public class SingleThreadCachingMessageCoderFactory<D extends BonaPortable, E extends BonaPortable> implements IMessageCoderFactory<D, E, byte []> {
    private final Map<String,IMessageDecoder<D, byte []>> decoders = new HashMap<String,IMessageDecoder<D, byte []>>(10);
    private final Map<String,IMessageEncoder<E, byte []>> encoders = new HashMap<String,IMessageEncoder<E, byte []>>(10);

    private final Class<D> decoderClass;
    private final Class<E> encoderClass;

    public SingleThreadCachingMessageCoderFactory(Class<D> decoderClass, Class<E> encoderClass) {
        this.decoderClass = decoderClass;
        this.encoderClass = encoderClass;
    }

    // override to add additional methods
    protected IMessageEncoder<E, byte []> createNewEncoderInstance(String mimeType) {
        if (mimeType.equals(MimeTypes.MIME_TYPE_BONAPARTE))
            return new BonaparteEncoder<E>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE))
            return new CompactBonaparteEncoder<E>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_JSON))
            return new JsonEncoder<E>();
        return null;
    }

    // override to add additional methods
    protected IMessageDecoder<D, byte []> createNewDecoderInstance(String mimeType) {
        if (mimeType.equals(MimeTypes.MIME_TYPE_BONAPARTE))
            return new BonaparteDecoder<D>(decoderClass);
        if (mimeType.equals(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE))
            return new CompactBonaparteDecoder<D>(decoderClass);
        if (mimeType.equals(MimeTypes.MIME_TYPE_JSON))
            return new JsonDecoder<D>(decoderClass);
        return null;
    }

    @Override
    public final IMessageEncoder<E, byte []> getEncoderInstance(String mimeType) {
        IMessageEncoder<E, byte []> encoder = encoders.get(mimeType);
        if (encoder != null)
            return encoder;
        encoder = createNewEncoderInstance(mimeType);
        if (encoder != null)
            encoders.put(mimeType, encoder);
        return encoder;
    }

    @Override
    public IMessageDecoder<D, byte []> getDecoderInstance(String mimeType) {
        IMessageDecoder<D, byte []> decoder = decoders.get(mimeType);
        if (decoder != null)
            return decoder;
        decoder = createNewDecoderInstance(mimeType);
        if (decoder != null)
            decoders.put(mimeType, decoder);
        return decoder;
    }
}
