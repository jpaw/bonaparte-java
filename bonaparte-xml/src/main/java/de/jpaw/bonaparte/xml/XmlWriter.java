package de.jpaw.bonaparte.xml;

import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.jpaw.bonaparte.core.AbstractMessageWriter;
import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;

/** Via XmlWriter, elements can be written sequentially, avoiding to keep everything in memory at once. */
public class XmlWriter extends AbstractMessageWriter<XMLStreamException> {

    private final boolean formatted;
    private final boolean fragment;
    private Marshaller m;
    private String dataTag;
    private XMLStreamWriter sw;
    
    public XmlWriter(Marshaller m, OutputStream os, boolean formatted, boolean fragment, String outerElementName) throws Exception {
        this.m = m;
        this.formatted = formatted;
        this.fragment = fragment;
        this.dataTag = outerElementName == null ? "data" : outerElementName;
        this.sw = XMLOutputFactory.newFactory().createXMLStreamWriter(os);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formatted));
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.valueOf(fragment));         // with XmlStreamWriter, fragments can be written
    }

    private void conditionalNewline(XMLStreamWriter sw) throws XMLStreamException {
        if (formatted) {
            if (getWriteCRs())
                sw.writeCharacters("\r");
            sw.writeCharacters("\n");
        }
    }

    @Override
    public void startTransmission() throws XMLStreamException {
        sw.writeStartDocument();
        conditionalNewline(sw);
        sw.writeStartElement(dataTag == null ? "data" : dataTag);
        conditionalNewline(sw);
    }

    @Override
    public void writeRecord(BonaCustom o) throws XMLStreamException {
        JAXBElement<BonaPortable> element = new JAXBElement<BonaPortable>(QName.valueOf(o.getClass().getSimpleName()), BonaPortable.class, (BonaPortable) o);
        try {
            m.marshal(element, sw);
        } catch (JAXBException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void terminateTransmission() throws XMLStreamException {
        sw.writeEndElement();
        sw.writeEndDocument();
        conditionalNewline(sw);
    }

    @Override
    public void writeObject(BonaCustom o) throws XMLStreamException {
        if (fragment)
            throw new UnsupportedOperationException("Cannot write single objects in streaming mode");
    }
}
