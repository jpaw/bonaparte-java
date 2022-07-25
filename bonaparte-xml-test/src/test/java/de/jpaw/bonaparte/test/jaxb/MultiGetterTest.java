package de.jpaw.bonaparte.test.jaxb;

import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3a;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3b;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3c;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

public class MultiGetterTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.test.jaxb";   // package name where jaxb.index sits

    private static String marshal(BonaPortable x) throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(x, writer);
        return writer.toString();
    }

    @Test
    public void marshall3a() throws Exception {
        System.out.println("Output 3a is " + marshal(new TestXml3a(42)));
    }

    @Test
    public void marshall3b() throws Exception {
        System.out.println("Output 3b is " + marshal(new TestXml3b(42)));
    }

    @Test
    public void marshall3c() throws Exception {
        System.out.println("Output 3c is " + marshal(new TestXml3c(42)));
    }
}
