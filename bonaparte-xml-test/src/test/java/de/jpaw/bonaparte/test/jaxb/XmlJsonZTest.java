package de.jpaw.bonaparte.test.jaxb;

import static org.testng.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import de.jpaw.bonaparte.pojos.test.jsonZ.TestJsonZ;
import de.jpaw.bonaparte.util.ToStringHelper;


public class XmlJsonZTest {
    private static final String PACKAGES = "de.jpaw.bonaparte.pojos.test.jsonZ:de.jpaw.bonaparte.xml";   // package name where jaxb.index sits
    private static final String EXPECTED_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<test_jsonZ:TestJsonZ xmlns:bon=\"http://www.jpaw.de/schema/bonaparte.xsd\" xmlns:test_jsonZ=\"http://www.jpaw.de/schema/test_jsonZ.xsd\">\n" +
            "    <test_jsonZ:num>12</test_jsonZ:num>" +
            "    <test_jsonZ:z>\n" +
            "        <bon:kvp>\n" +
            "            <bon:key>isGreen</bon:key>\n" +
            "            <bon:bool>true</bon:bool>\n" +
            "        </bon:kvp>\n" +
            "        <bon:kvp>\n" +
            "            <bon:key>hello</bon:key>\n" +
            "            <bon:value>world</bon:value>\n" +
            "        </bon:kvp>\n" +
            "    </test_jsonZ:z>\n" +
            "    <test_jsonZ:str>ABC</test_jsonZ:str>" +
            "</test_jsonZ:TestJsonZ>\n";
    private static final String EXPECTED_JSON = "{\"num\":12,\"z\":{\"isGreen\":true,\"hello\":\"world\"},\"str\":\"ABC\"}";

    public TestJsonZ buildObject() {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("hello", "world");
        map.put("isGreen", true);

        TestJsonZ obj = new TestJsonZ();
        obj.setNum(12);
        obj.setZ(map);
        obj.setStr("ABC");
        return obj;
    }

    @Test
    public void marshallXml() throws Exception {
        final TestJsonZ obj = buildObject();

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

    @Test
    public void unmarshallXml() throws Exception {
        // create the XML for this
        JAXBContext context = JAXBContext.newInstance(PACKAGES);

        Unmarshaller unmarshaller = context.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(new StringReader(EXPECTED_XML));

        assert(obj != null);
        assert(obj instanceof TestJsonZ);
        TestJsonZ obj2 = (TestJsonZ)obj;
        System.out.println("Output is " + ToStringHelper.toStringML(obj2));
        assert("world".equals(obj2.getZ().get("hello")));
    }

    @Test
    public void createXmlSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGES);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        assert(sb != null);
        System.out.println("Schema is " + sb);
    }

    @Test
    public void marshallJson() throws Exception {
        final TestJsonZ obj = buildObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        String serialized = mapper.writer().writeValueAsString(obj);
        System.out.println("Jackson2 produces " + serialized);
        assertEquals(EXPECTED_JSON, serialized);
    }

    @Test
    public void unmarshallJson() throws Exception {
        final TestJsonZ objIn = buildObject();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        TestJsonZ obj = mapper.readValue(EXPECTED_JSON, TestJsonZ.class);
        assert(obj != null);
        System.out.println("Output is " + ToStringHelper.toStringML(obj));
        assert("world".equals(obj.getZ().get("hello")));
        assertEquals(obj, objIn);
    }
}
