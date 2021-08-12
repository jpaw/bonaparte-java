package de.jpaw.bonaparte.jaxrs;

import java.util.Collection;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.MimeTypes;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(MimeTypes.MIME_TYPE_BONAPARTE)
public class BonaparteJaxRsCollectionConverter extends AbstractBonaparteConverter<Collection<BonaPortable>, RuntimeException> {

    public BonaparteJaxRsCollectionConverter() {
        super(MimeTypes.MIME_TYPE_BONAPARTE, Collection.class);
    }

    @Override
    protected BufferedMessageWriter<RuntimeException> newComposerWithData(Collection<BonaPortable> obj) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeTransmission(obj);
        return bac;
    }
}
