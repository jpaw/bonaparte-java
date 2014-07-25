package de.jpaw.bonaparte.vertx.test;

import org.testng.annotations.Test;
import org.vertx.java.core.json.JsonObject;

import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.XEnumDefinition;
import de.jpaw.bonaparte.pojos.testobjects.XColor;
import de.jpaw.bonaparte.vertx.JsonObjectComposer;
import de.jpaw.util.ToStringHelper;

public class JsonConversionTest {

    // Maps are not yet supported (JSON does not support them natively, we have to supply emulator classes)
    // @Test
    public void testConversionMap() throws Exception {
        ClassDefinition cls = ClassDefinition.class$MetaData();
        JsonObject clsJ = JsonObjectComposer.toJsonObject(cls);
        
        System.out.println(ToStringHelper.toStringML(cls));
        
        System.out.println(clsJ.encodePrettily());
    }

    @Test
    public void testConversionEnum() throws Exception {
        XEnumDefinition xc = XColor.xenum$MetaData();
        JsonObject xcJ = JsonObjectComposer.toJsonObject(xc);
        
        System.out.println(ToStringHelper.toStringML(xc));
        
        System.out.println(xcJ.encodePrettily());
    }
}

