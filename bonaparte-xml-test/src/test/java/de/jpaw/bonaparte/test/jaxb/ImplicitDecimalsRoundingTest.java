package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.test.jaxb.TestXml2;


@Test
public class ImplicitDecimalsRoundingTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.test.jaxb";   // package name where jaxb.index sits
    private static final String ROUNDED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
          + "<testXml2>\n"
          + "    <digits2>42</digits2>\n"
          + "    <microUnits>42.123456000000</microUnits>\n"
          + "    <roundedMillis>333.66688</roundedMillis>\n"
          + "</testXml2>\n";
    private static final String NOTROUNDED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
          + "<testXml2>\n"
          + "    <digits2>42</digits2>\n"
          + "    <microUnits>42.1234562</microUnits>\n"
          + "    <roundedMillis>333.666</roundedMillis>\n"
          + "</testXml2>\n";
    
    public void unmarshallXenumWithRounding() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new DefaultValidationEventHandler());
        Object obj = unmarshaller.unmarshal(new StringReader(ROUNDED_XML));
        
        assert(obj != null);
        assert(obj instanceof TestXml2);
        TestXml2 obj2 = (TestXml2)obj;
        assert(obj2.digits2 == (byte)42);
        assert(obj2.microUnits == 42123456L);
        assert(obj2.roundedMillis == 333667);
    }
    
    public void unmarshallXenumWithRoundingException() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        Object obj = null;
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new DefaultValidationEventHandler());
            obj = unmarshaller.unmarshal(new StringReader(NOTROUNDED_XML));
            System.out.println("Not good, I expected an exception, but got " + obj);
            throw new Exception("missing exception in case of rounding where forbidden");
        } catch (javax.xml.bind.UnmarshalException e) {
            System.out.println("Good, caught the expected exception: " + e);
        }
    }

}
