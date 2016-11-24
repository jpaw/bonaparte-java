package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.test.jaxb.TestXml2Up;

@Test
public class UppercaseNamesTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.test.jaxb";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<TestXml2Up>\n" +
            "    <Digits2>88</Digits2>\n" + 
            "    <MicroUnits>0.545454</MicroUnits>\n" +
            "</TestXml2Up>\n";

    public void marshallUpcase() throws Exception {
        TestXml2Up obj = new TestXml2Up();
        obj.setDigits2((byte)88);
        obj.setMicroUnits(545454L);

        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);

        System.out.println("Output is " + writer);
        String actualXml = writer.toString().replace("\r", "");
        assert(actualXml.equals(EXPECTED_XML));
    }

    public void unmarshallUpcase() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new StringReader(EXPECTED_XML));

        assert(obj != null);
        assert(obj instanceof TestXml2Up);
        TestXml2Up obj2 = (TestXml2Up)obj;
        assert(obj2.getDigits2() == (byte)88);
        assert(obj2.getMicroUnits() == 545454L);
    }

    public void createUpcaseSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        assert(sb != null);
        System.out.println("Schema is " + sb);
    }
}
