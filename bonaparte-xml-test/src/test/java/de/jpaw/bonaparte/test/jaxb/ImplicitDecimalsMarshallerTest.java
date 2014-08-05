package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.test.jaxb.TestXml2;


@Test
public class ImplicitDecimalsMarshallerTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.test.jaxb";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
          + "<testXml2>\n"
          + "    <digits2>42</digits2>\n"
          + "    <microUnits>42.123456</microUnits>\n"
          + "    <roundedMillis>333.666</roundedMillis>\n"
          + "</testXml2>\n";
    
    public void marshallTestXml() throws Exception {
        TestXml2 obj = new TestXml2((byte)42, 42123456L, 333666);
        
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
    
    public void unmarshallXenum() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new StringReader(EXPECTED_XML));
        
        assert(obj != null);
        assert(obj instanceof TestXml2);
        TestXml2 obj2 = (TestXml2)obj;
        assert(obj2.digits2 == (byte)42);
        assert(obj2.microUnits == 42123456L);
        assert(obj2.roundedMillis == 333666);
    }

}
