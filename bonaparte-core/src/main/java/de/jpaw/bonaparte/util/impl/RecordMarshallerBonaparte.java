package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public class RecordMarshallerBonaparte implements IMarshaller {
    
    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_BONAPARTE;
    }

    @Override
    public ByteArray marshal(BonaPortable request) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(request);
        return new ByteArray(bac.getBuffer(), 0, bac.getLength());
    }

    @Override
    public BonaPortable unmarshal(ByteBuilder buffer) throws ApplicationException {
        return (new ByteArrayParser(buffer.getCurrentBuffer(), 0, buffer.length())).readRecord();
    }
}
