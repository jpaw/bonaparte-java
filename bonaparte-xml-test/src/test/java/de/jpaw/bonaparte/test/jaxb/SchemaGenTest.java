package de.jpaw.bonaparte.test.jaxb;

import jakarta.xml.bind.JAXBContext;

import org.testng.annotations.Test;

@Test
public class SchemaGenTest {
    private static final String PACKAGE = "com.foo.test.jaxb.schema";       // package name where jaxb.index sits


    public void createSchema() throws Exception {
        JAXBContext context = JAXBContext.newInstance(PACKAGE);
        DemoSchemaWriter sor = new DemoSchemaWriter();
        context.generateSchema(sor);

        StringBuffer sb = sor.strwr.getBuffer();
        assert(sb != null);
        System.out.println("Schema is " + sb);
    }
}
