package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;

@Provider
@Produces(CompactByteArrayComposer.MIME_TYPE)
public class CompactJaxRsConverter extends AbstractBonaparteConverters<IOException> {

    public CompactJaxRsConverter() {
        super(CompactByteArrayComposer.MIME_TYPE);
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
