package de.jpaw.bonaparte.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.jpaw.bonaparte.core.Settings;

public class XmlComposer extends Settings {
//    private static final byte [] XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\", standalone=\"yes\"?>\r\n<data>\r\n".getBytes();
//    private static final byte [] XML_FOOTER = "</data>\r\n".getBytes();

//    private final JAXBContext ctx;
    private final boolean formatted;
    private Marshaller m;
    
    public XmlComposer(JAXBContext ctx, boolean formatted, boolean fragment) throws JAXBException {
//        this.ctx = ctx;
        this.formatted = formatted;
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
    
    private void conditionalNewline(XMLStreamWriter sw) throws XMLStreamException {
        if (formatted) {
            if (doWriteCRs())
                sw.writeCharacters("\r");
            sw.writeCharacters("\n");
        }
    }
    
    public void startDocument(XMLStreamWriter sw, String dataTag) throws XMLStreamException {
        sw.writeStartDocument();
        conditionalNewline(sw);
        sw.writeStartElement(dataTag == null ? "data" : dataTag);
        conditionalNewline(sw);
    }
    
    public void endDocument(XMLStreamWriter sw) throws XMLStreamException {
        sw.writeEndElement();
        sw.writeEndDocument();
        conditionalNewline(sw);
    }
}
