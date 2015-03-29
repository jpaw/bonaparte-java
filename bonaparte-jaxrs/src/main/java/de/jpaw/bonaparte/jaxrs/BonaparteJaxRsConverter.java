package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.util.ByteBuilder;

@Provider
@Produces(ByteArrayComposer.MIME_TYPE)
public class BonaparteJaxRsConverter implements MessageBodyReader<BonaPortable>, MessageBodyWriter<BonaPortable> {
    private final Logger LOGGER = LoggerFactory.getLogger(BonaparteJaxRsConverter.class);
    public static int MAXIMUM_MESSAGE_LENGTH    = -1;               // tunable constant (-1 = disabled)
    public static int READ_CHUNK_SIZE           = 4000;             // tunable constant

    @Override
    public long getSize(BonaPortable obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        return -1;      // unknown size
    }

    @Override
    public boolean isWriteable(Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        LOGGER.trace("Check for writing class {} as {}", cls.getCanonicalName(), mediaType.toString());
        return ByteArrayComposer.MIME_TYPE.equals(mediaType.toString()) && BonaPortable.class.isAssignableFrom(cls);
    }

    @Override
    public void writeTo(BonaPortable obj, Class<?> cls, Type type, Annotation[] anno, MediaType mediaType, MultivaluedMap<String, Object> args,
            OutputStream os) throws IOException, WebApplicationException {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(obj);
        os.write(bac.getBuffer(), 0, bac.getLength());
        LOGGER.trace("Serialized instance of {} as {} bytes", obj.getClass().getCanonicalName(), bac.getLength());
    }

    @Override
    public boolean isReadable(Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        LOGGER.trace("Check for reading class {} as {}", cls.getCanonicalName(), mediaType.toString());
        return ByteArrayComposer.MIME_TYPE.equals(mediaType.toString()) && BonaPortable.class.isAssignableFrom(cls);
    }

    @Override
    public BonaPortable readFrom(Class<BonaPortable> cls, Type type, Annotation[] anno, MediaType mediaType, MultivaluedMap<String, String> args,
            InputStream is) throws IOException, WebApplicationException {
        // we may need to compose the full message from several parts
        ByteBuilder buffer = new ByteBuilder(0, ByteArrayParser.UTF8_CHARSET);
        final byte [] tmp = new byte [READ_CHUNK_SIZE]; 
        // do a loop here, because an initial read may not return the full number of bytes
        for (;;) {
            int lastRead = is.read(tmp, 0, READ_CHUNK_SIZE);
            LOGGER.trace("read chunk returned {} byte", lastRead);
            if (lastRead < 0) {
                break; // EOF
            }
            if (lastRead > 0) {
                if (MAXIMUM_MESSAGE_LENGTH > 0) {
                    if (buffer.length() + lastRead > MAXIMUM_MESSAGE_LENGTH)
                        throw new WebApplicationException("message length exceeds maximum allowed size of " + MAXIMUM_MESSAGE_LENGTH + " byte",
                                Response.Status.BAD_REQUEST);
                }
                buffer.append(tmp, 0, lastRead);
                if (tmp[lastRead - 1] == '\n') {
                    LOGGER.trace("got NL as last character");
                    break;
                }
            }
        }
        int len = buffer.length();
        LOGGER.debug("received message of {} bytes, terminator{} found", len, buffer.getCurrentBuffer()[len-1] == '\n' ? "" : " NOT");
        ByteArrayParser bap = new ByteArrayParser(buffer.getCurrentBuffer(), 0, len);
        try {
            BonaPortable result = bap.readRecord();
            LOGGER.debug("Parsed object of type {}" + result.ret$PQON());
            return result;
        } catch (MessageParserException e) {
            // provide the parser message via http response
            LOGGER.debug("Parsing resulted in error {}", e.getMessage());
            throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }
}
