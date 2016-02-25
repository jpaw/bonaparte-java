package de.jpaw.bonaparte.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;

import de.jpaw.bonaparte.core.Settings;

/** Simple static methods to marshal to Xml. */
public class XmlComposer extends Settings {
    private Marshaller m;

    public XmlComposer(JAXBContext ctx, boolean formatted, boolean fragment) throws JAXBException {
        m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formatted));
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(fragment));         // with XmlStreamWriter, fragments can be written
    }

    public void marshal(Object obj, OutputStream os) throws JAXBException {
        m.marshal(obj, os);
    }

    public void marshal(Object obj, XMLStreamWriter sw) throws JAXBException {
        m.marshal(obj, sw);
    }

    public String marshal(Object obj) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
        m.marshal(obj, baos);
        try {
            return new String(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
