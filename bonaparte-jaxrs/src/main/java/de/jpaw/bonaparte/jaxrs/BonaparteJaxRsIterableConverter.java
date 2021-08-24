package de.jpaw.bonaparte.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.MimeTypes;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(MimeTypes.MIME_TYPE_BONAPARTE)
public class BonaparteJaxRsIterableConverter extends AbstractBonaparteConverter<Iterable<BonaPortable>, RuntimeException> {

    public BonaparteJaxRsIterableConverter() {
        super(MimeTypes.MIME_TYPE_BONAPARTE, Iterable.class);
    }

    @Override
    protected BufferedMessageWriter<RuntimeException> newComposerWithData(Iterable<BonaPortable> obj) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeTransmission(obj);
        return bac;
    }
}
