package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(ByteArrayComposer.MIME_TYPE)
public class BonaparteJaxRsCollectionConverter implements MessageBodyWriter<Collection<BonaPortable>> {
    private final Logger LOGGER = LoggerFactory.getLogger(BonaparteJaxRsCollectionConverter.class);

    @Override
    public long getSize(Collection<BonaPortable> obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        return -1;      // unknown size
    }

    @Override
    public boolean isWriteable(Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        LOGGER.trace("Check for writing class {} as {}", cls.getCanonicalName(), mediaType.toString());
        return ByteArrayComposer.MIME_TYPE.equals(mediaType.toString()) && Collection.class.isAssignableFrom(cls);
    }

    @Override
    public void writeTo(Collection<BonaPortable> obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType, MultivaluedMap<String, Object> args,
            OutputStream os) throws IOException, WebApplicationException {
        ByteArrayComposer bac = new ByteArrayComposer();
//        bac.startTransmission();
        for (BonaPortable o : obj)
            bac.writeRecord(o);
//        bac.terminateTransmission();
        os.write(bac.getBuffer(), 0, bac.getLength());
        LOGGER.trace("Serialized instance of {} as {} bytes", obj.getClass().getCanonicalName(), bac.getLength());
    }
}
