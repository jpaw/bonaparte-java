package de.jpaw.bonaparte.api.codecs.impl;

import java.util.HashMap;
import java.util.Map;

import de.jpaw.bonaparte.api.codecs.IMessageCoderFactory;
import de.jpaw.bonaparte.api.codecs.IMessageDecoder;
import de.jpaw.bonaparte.api.codecs.IMessageEncoder;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MimeTypes;


public class SingleThreadCachingRecordCoderFactory implements IMessageCoderFactory<BonaPortable, BonaPortable, byte []> {
    private final Map<String,IMessageDecoder<BonaPortable, byte []>> decoders = new HashMap<String,IMessageDecoder<BonaPortable, byte []>>(10);
    private final Map<String,IMessageEncoder<BonaPortable, byte []>> encoders = new HashMap<String,IMessageEncoder<BonaPortable, byte []>>(10);

    // override to add additional methods
    protected IMessageEncoder<BonaPortable, byte []> createNewEncoderInstance(String mimeType) {
        if (mimeType.equals(MimeTypes.MIME_TYPE_BONAPARTE))
            return new BonaparteRecordEncoder<BonaPortable>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE))
            return new CompactRecordEncoder<BonaPortable>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_JSON))
            return new JsonEncoder<BonaPortable>();
        return null;
    }

    // override to add additional methods
    protected IMessageDecoder<BonaPortable, byte []> createNewDecoderInstance(String mimeType) {
        if (mimeType.equals(MimeTypes.MIME_TYPE_BONAPARTE))
            return new BonaparteRecordDecoder<BonaPortable>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE))
            return new CompactRecordDecoder<BonaPortable>();
        if (mimeType.equals(MimeTypes.MIME_TYPE_JSON))
            return new JsonDecoder<BonaPortable>(BonaPortable.class);
        return null;
    }

    @Override
    public final IMessageEncoder<BonaPortable, byte []> getEncoderInstance(String mimeType) {
        IMessageEncoder<BonaPortable, byte []> encoder = encoders.get(mimeType);
        if (encoder != null)
            return encoder;
        encoder = createNewEncoderInstance(mimeType);
        if (encoder != null)
            encoders.put(mimeType, encoder);
        return encoder;
    }

    @Override
    public IMessageDecoder<BonaPortable, byte []> getDecoderInstance(String mimeType) {
        IMessageDecoder<BonaPortable, byte []> decoder = decoders.get(mimeType);
        if (decoder != null)
            return decoder;
        decoder = createNewDecoderInstance(mimeType);
        if (decoder != null)
            decoders.put(mimeType, decoder);
        return decoder;
    }
}
