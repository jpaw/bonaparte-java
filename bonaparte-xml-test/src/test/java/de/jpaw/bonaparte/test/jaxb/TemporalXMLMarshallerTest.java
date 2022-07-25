package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.test.jaxbtemporal.TestFieldXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;


public class TemporalXMLMarshallerTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.test.jaxbtemporal";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<test_jaxbtemporal:TestFieldXml xmlns:test_jaxbtemporal=\"http://www.jpaw.de/schema/test_jaxbtemporal.xsd\">\n" +
            "    <test_jaxbtemporal:myDate>2015-03-07</test_jaxbtemporal:myDate>\n" +
            "    <test_jaxbtemporal:myDateTime>2015-03-07T18:14:55.000</test_jaxbtemporal:myDateTime>\n" +
            "    <test_jaxbtemporal:myTime>18:34:55.000</test_jaxbtemporal:myTime>\n" +
            "</test_jaxbtemporal:TestFieldXml>\n";

    @Test
    public void marshallTemporal() throws Exception {
        TestFieldXml obj = new TestFieldXml();
        obj.myDate = LocalDate.of(2015, 3, 7);
        obj.myTime = LocalTime.of(18, 34, 55);
        obj.myDateTime = LocalDateTime.of(2015, 3, 7, 18, 14, 55);

        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);

        System.out.println("Output is " + writer);
        String actualXml = writer.toString().replace("\r", "");
        Assertions.assertEquals(EXPECTED_XML, actualXml);
    }

    @Test
    public void unmarshallTemporal() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new StringReader(EXPECTED_XML));

        Assertions.assertNotNull(obj);
        Assertions.assertTrue(obj instanceof TestFieldXml);
        TestFieldXml obj2 = (TestFieldXml)obj;
        Assertions.assertEquals(LocalDate.of(2015, 3, 7), obj2.myDate);
        Assertions.assertEquals(LocalTime.of(18, 34, 55), obj2.myTime);
        Assertions.assertEquals(LocalDateTime.of(2015, 3, 7, 18, 14, 55), obj2.myDateTime);
    }

    @Test
    public void createTemporalSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        Assertions.assertNotNull(sb);
        System.out.println("Schema is " + sb);
    }
}
