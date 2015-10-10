package de.jpaw.bonaparte.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;

@Provider
@Produces(ByteArrayComposer.MIME_TYPE)
public class BonaparteJaxRsConverter extends AbstractBonaparteConverters<RuntimeException> {

    public BonaparteJaxRsConverter() {
        super(ByteArrayComposer.MIME_TYPE);
    }

    @Override
    protected MessageParser<MessageParserException> newParser(byte[] buffer, int offset, int len) {
        return new ByteArrayParser(buffer, offset, len);
    }

    @Override
    protected BufferedMessageWriter<RuntimeException> newComposerWithData(BonaPortable obj) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(obj);
        return bac;
    }
}