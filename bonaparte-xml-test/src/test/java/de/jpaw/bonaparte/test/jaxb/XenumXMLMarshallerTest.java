package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.xenumJaxb.TestEnum;
import de.jpaw.bonaparte.pojos.xenumJaxb.TestXmlXenumWithAdapter;
import de.jpaw.bonaparte.pojos.xenumJaxb.XEnumUse;

@Test
public class XenumXMLMarshallerTest {
    private static final String PACKAGE = "de.jpaw.bonaparte.pojos.xenumJaxb";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xEnumUse>\n    <daddel>G</daddel>\n</xEnumUse>\n";

    public void marshallXenum() throws Exception {
        XEnumUse obj = new XEnumUse();
        obj.setDaddel(TestEnum.GREEN);

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
        assert(obj instanceof XEnumUse);
        XEnumUse obj2 = (XEnumUse)obj;
        TestXmlXenumWithAdapter xenum = obj2.getDaddel();
        assert(xenum != null);
        assert("G".equals(xenum.getToken()));
    }

    public void createXenumSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        assert(sb != null);
        System.out.println("Schema is " + sb);
    }
}
