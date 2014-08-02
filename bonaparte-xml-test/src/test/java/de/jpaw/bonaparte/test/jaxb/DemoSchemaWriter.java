package de.jpaw.bonaparte.test.jaxb;

import java.io.StringWriter;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class DemoSchemaWriter extends SchemaOutputResolver {
    public final StringWriter strwr = new StringWriter();

    public Result createOutput(String namespaceURI, String suggestedFileName) {
        StreamResult sr = new StreamResult(strwr);
        sr.setSystemId("mine");
        return sr;
    }
}
