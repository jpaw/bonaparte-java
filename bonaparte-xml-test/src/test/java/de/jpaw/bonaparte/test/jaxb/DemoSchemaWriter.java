package de.jpaw.bonaparte.test.jaxb;

import java.io.StringWriter;

import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class DemoSchemaWriter extends SchemaOutputResolver {
    public final StringWriter strwr = new StringWriter();

    @Override
    public Result createOutput(String namespaceURI, String suggestedFileName) {
        StreamResult sr = new StreamResult(strwr);
        sr.setSystemId("mine");
        return sr;
    }
}
