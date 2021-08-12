package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyReader;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public abstract class AbstractBonaparteConverters<E extends Exception> extends AbstractBonaparteConverter<BonaPortable, E> implements MessageBodyReader<BonaPortable> {
    public static int MAXIMUM_MESSAGE_LENGTH    = -1;               // tunable constant (-1 = disabled)
    public static int READ_CHUNK_SIZE           = 4000;             // tunable constant

    public AbstractBonaparteConverters(String supportedMimeType) {
        super(supportedMimeType, BonaPortable.class);
    }

    protected abstract MessageParser<MessageParserException> newParser(byte [] buffer, int offset, int len);

    @Override
    public boolean isReadable(Class<?> cls, Type type, Annotation[] anno, MediaType mediaType) {
        LOGGER.trace("Check for reading class {} as {}", cls.getCanonicalName(), mediaType.toString());
        return supportedMimeType.equals(mediaType.toString()) && supportedClass.isAssignableFrom(cls);
    }

    @Override
    public BonaPortable readFrom(Class<BonaPortable> cls, Type type, Annotation[] anno, MediaType mediaType, MultivaluedMap<String, String> args,
            InputStream is) throws IOException, WebApplicationException {
        // we may need to compose the full message from several parts
        ByteBuilder buffer = new ByteBuilder(0, ByteArray.CHARSET_UTF8);
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
                buffer.write(tmp, 0, lastRead);
                if (tmp[lastRead - 1] == '\n') {
                    LOGGER.trace("got NL as last character");
                    break;
                }
            }
        }
        int len = buffer.length();
        LOGGER.debug("received message of {} bytes, terminator{} found", len, buffer.getCurrentBuffer()[len-1] == '\n' ? "" : " NOT");
        MessageParser<MessageParserException> bap = newParser(buffer.getCurrentBuffer(), 0, len);
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
