package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.MimeTypes;

@Provider
@Produces(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE)
public class CompactJaxRsConverter extends AbstractBonaparteConverters<IOException> {

    public CompactJaxRsConverter() {
        super(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE);
    }

    @Override
    protected MessageParser<MessageParserException> newParser(byte[] buffer, int offset, int len) {
        return new CompactByteArrayParser(buffer, offset, len);
    }

    @Override
    protected BufferedMessageWriter<IOException> newComposerWithData(BonaPortable obj) {
        CompactByteArrayComposer bac = new CompactByteArrayComposer();
        bac.writeRecord(obj);
        return bac;
    }
}
