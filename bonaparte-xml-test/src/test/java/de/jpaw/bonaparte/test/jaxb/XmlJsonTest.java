package de.jpaw.bonaparte.test.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.test.json.Test4XML;
import de.jpaw.bonaparte.util.ToStringHelper;


@Test
public class XmlJsonTest {
    private static final String PACKAGES = "de.jpaw.bonaparte.pojos.test.json:de.jpaw.bonaparte.xml";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<test_json:Test4XML xmlns:bon=\"http://www.jpaw.de/schema/bonaparte.xsd\" xmlns:test_json=\"http://www.jpaw.de/schema/test_json.xsd\">\n" +
            "<test_json:obj xsi:type=\"xs:double\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">3.15</test_json:obj>\n" +
            "    <test_json:jsono>\n" +
            "        <bon:kvp>\n" +
            "            <bon:key>isGreen</bon:key>\n" +
            "            <bon:bool>true</bon:bool>\n" +
            "        </bon:kvp>\n" +
            "        <bon:kvp>\n" +
            "            <bon:key>hello</bon:key>\n" +
            "            <bon:value>world</bon:value>\n" +
            "        </bon:kvp>\n" +
            "    </test_json:jsono>\n" +
            "</test_json:Test4XML>\n";

    public void marshallJson() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("hello", "world");
        map.put("isGreen", true);

        Test4XML obj = new Test4XML();
        obj.setFirst("Hello");
        obj.setObj(3.15);
        obj.setJsono(map);

        List many = new ArrayList<Object>(8);
        many.add(3.55);
        many.add(true);
        many.add(null);
        many.add("bye");
        obj.setMany(many);

        obj.setArr(many);
        obj.setLast("world");

        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGES);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);

        System.out.println("Output is " + writer);
        String actualXml = writer.toString().replace("\r", "");
        // assert(actualXml.equals(EXPECTED_XML));
    }

    public void unmarshallJson() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGES);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new StringReader(EXPECTED_XML));

        assert(obj != null);
        assert(obj instanceof Test4XML);
        Test4XML obj2 = (Test4XML)obj;
        System.out.println("Output is " + ToStringHelper.toStringML(obj2));
        assert("world".equals(obj2.getJsono().get("hello")));
    }

    public void createJsonSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGES);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        assert(sb != null);
        System.out.println("Schema is " + sb);
    }

}
