package de.jpaw.bonaparte.jaxrs;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BufferedMessageWriter;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;

// converter for a list or set of BonaPortables. Unfortunately, due to type erasure, we cannot really verify the element types by the class reference
@Provider
@Produces(CompactByteArrayComposer.MIME_TYPE)
public class CompactJaxRsIterableConverter extends AbstractBonaparteConverter<Iterable<BonaPortable>> {

    public CompactJaxRsIterableConverter() {
        super(CompactByteArrayComposer.MIME_TYPE, Iterable.class);
    }

    @Override
    protected BufferedMessageWriter<RuntimeException> newComposerWithData(Iterable<BonaPortable> obj) {
        CompactByteArrayComposer bac = new CompactByteArrayComposer();
        bac.writeTransmission(obj);
        return bac;
    }
}
