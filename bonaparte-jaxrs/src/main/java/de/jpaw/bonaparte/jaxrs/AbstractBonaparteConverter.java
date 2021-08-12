package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BufferedMessageWriter;

public abstract class AbstractBonaparteConverter<T, E extends Exception> implements MessageBodyWriter<T> {
    protected final Logger LOGGER = LoggerFactory.getLogger(AbstractBonaparteConverter.class);
    protected final String supportedMimeType;
    protected final Class<?> supportedClass;

    public AbstractBonaparteConverter(String supportedMimeType, Class<?> supportedClass) {
        this.supportedMimeType = supportedMimeType;
        this.supportedClass = supportedClass;
    }

    protected abstract BufferedMessageWriter<E> newComposerWithData(T obj);

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
        BufferedMessageWriter<E> mc = newComposerWithData(obj);
        os.write(mc.getBuffer(), 0, mc.getLength());
        LOGGER.trace("{}: Serialized instance of {} as {} bytes", this.getClass().getSimpleName(), obj.getClass().getCanonicalName(), mc.getLength());
    }
}
