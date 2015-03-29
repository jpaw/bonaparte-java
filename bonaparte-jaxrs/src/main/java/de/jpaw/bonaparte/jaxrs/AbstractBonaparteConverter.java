package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BufferedMessageComposer;

public abstract class AbstractBonaparteConverter<T> implements MessageBodyWriter<T> {
    protected final Logger LOGGER = LoggerFactory.getLogger(AbstractBonaparteConverter.class);
    protected final String supportedMimeType;
    protected final Class<?> supportedClass;
    
    public AbstractBonaparteConverter(String supportedMimeType, Class<?> supportedClass) {
        this.supportedMimeType = supportedMimeType;
        this.supportedClass = supportedClass;
    }

    protected abstract BufferedMessageComposer<RuntimeException> newComposerWithData(T obj);
    
    @Override
    public long getSize(T obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        return -1;      // unknown size
    }

    @Override
    public boolean isWriteable(Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        LOGGER.trace("Check for writing class {} as {}", cls.getCanonicalName(), mediaType.toString());
        return supportedMimeType.equals(mediaType.toString()) && supportedClass.isAssignableFrom(cls);
    }

    @Override
    public void writeTo(T obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType, MultivaluedMap<String, Object> args,
            OutputStream os) throws IOException, WebApplicationException {
        BufferedMessageComposer<RuntimeException> mc = newComposerWithData(obj);
        os.write(mc.getBuffer(), 0, mc.getLength());
        LOGGER.trace("{}: Serialized instance of {} as {} bytes", this.getClass().getSimpleName(), obj.getClass().getCanonicalName(), mc.getLength());
    }
}
