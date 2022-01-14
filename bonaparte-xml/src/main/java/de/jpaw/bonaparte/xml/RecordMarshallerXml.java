package de.jpaw.bonaparte.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.util.IMarshaller;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;
import de.jpaw.util.ByteBuilderOutputStream;

public class RecordMarshallerXml implements IMarshaller {

    private final JAXBContext context;
    private final boolean formatted;

    /** Constructor creates the class instance, which builds the thread safe JAXB context.
     * @throws JAXBException */
    public RecordMarshallerXml(JAXBContext context, boolean formatted) throws JAXBException {
        this.context = context;
        this.formatted = formatted;
    }

    @Override
    public String getContentType() {
        return MimeTypes.MIME_TYPE_XML;
    }

    @Override
    public ByteArray marshal(BonaPortable request) throws Exception {
        Marshaller m = context.createMarshaller();
        if (formatted)
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  // for testing purposes - do not use in production
        // create an outputStream to marshal to, into a byte array
        ByteBuilderOutputStream os = new ByteBuilderOutputStream(16000);
        m.marshal(request, os);
        return os.asByteArray();
    }

    @Override
    public BonaPortable unmarshal(ByteBuilder buffer) throws Exception {
        Unmarshaller u = context.createUnmarshaller();
        return (BonaPortable)u.unmarshal(buffer.asByteArrayInputStream());
    }
}
