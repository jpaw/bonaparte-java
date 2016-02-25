package de.jpaw.bonaparte.util.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

public class MarshallerCompactBonaparte implements IMarshaller {

    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_COMPACT_BONAPARTE;
    }

    @Override
    public ByteArray marshal(BonaPortable request) {
        return CompactByteArrayComposer.marshalAsByteArray(StaticMeta.OUTER_BONAPORTABLE, request);
    }

    @Override
    public BonaPortable unmarshal(ByteBuilder buffer) throws ApplicationException {
        return (new CompactByteArrayParser(buffer.getCurrentBuffer(), 0, buffer.length())).readObject(StaticMeta.OUTER_BONAPORTABLE, BonaPortable.class);
    }
}
