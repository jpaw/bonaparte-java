package de.jpaw.bonaparte.jaxrs;

import java.io.IOException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.MimeTypes;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE)
public class CompactJaxRsIterableConverter extends AbstractBonaparteConverter<Iterable<BonaPortable>, IOException> {

    public CompactJaxRsIterableConverter() {
        super(MimeTypes.MIME_TYPE_COMPACT_BONAPARTE, Iterable.class);
    }

    @Override
    protected BufferedMessageWriter<IOException> newComposerWithData(Iterable<BonaPortable> obj) {
        CompactByteArrayComposer bac = new CompactByteArrayComposer();
        bac.writeTransmission(obj);
        return bac;
    }
}
