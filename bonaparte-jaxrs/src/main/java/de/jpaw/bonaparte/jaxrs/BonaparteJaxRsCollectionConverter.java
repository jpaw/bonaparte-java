package de.jpaw.bonaparte.jaxrs;

import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.ByteArrayComposer;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(ByteArrayComposer.MIME_TYPE)
public class BonaparteJaxRsCollectionConverter extends AbstractBonaparteConverter<Collection<BonaPortable>> {

    public BonaparteJaxRsCollectionConverter() {
        super(ByteArrayComposer.MIME_TYPE, Collection.class);
    }

    @Override
    protected BufferedMessageWriter<RuntimeException> newComposerWithData(Collection<BonaPortable> obj) {
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeTransmission(obj);
        return bac;
    }
}
