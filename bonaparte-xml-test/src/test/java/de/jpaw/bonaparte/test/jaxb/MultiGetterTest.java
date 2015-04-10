package de.jpaw.bonaparte.test.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3a;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3b;
import de.jpaw.bonaparte.pojos.test.jaxb.TestXml3c;

@Test
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
    
    public void marshall3a() throws Exception {
        System.out.println("Output 3a is " + marshal(new TestXml3a(42)));
    }

    public void marshall3b() throws Exception {
        System.out.println("Output 3b is " + marshal(new TestXml3b(42)));
    }

    public void marshall3c() throws Exception {
        System.out.println("Output 3c is " + marshal(new TestXml3c(42)));
    }

}
