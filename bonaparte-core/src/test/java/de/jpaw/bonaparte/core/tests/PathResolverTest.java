package de.jpaw.bonaparte.core.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortableFactory;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.XEnumSetDefinition;
import de.jpaw.bonaparte.util.FieldGetter;

public class PathResolverTest {
    static {
        BonaPortableFactory.init();       // required here, tests will fail if omitted
    }

    private void info() {
        System.out.println(ClassDefinition.meta$$fields.getLowerBound() == null ? "NULL!!!!!" : "ok");
    }

    @Test
    public void testPathResolving1() throws Exception {
        ClassDefinition obj = XEnumSetDefinition.BClass.INSTANCE.getMetaData();
        
        FieldDefinition f = FieldGetter.getFieldDefinitionForPathname(obj, "baseXEnum");
        Assert.assertEquals(f, XEnumSetDefinition.meta$$baseXEnum);
    }
    
    @Test
    public void testPathResolving2() throws Exception {
        ClassDefinition obj = ClassDefinition.BClass.INSTANCE.getMetaData();
        
        FieldDefinition f = FieldGetter.getFieldDefinitionForPathname(obj, "fields[2].dataCategory");
        Assert.assertEquals(f, FieldDefinition.meta$$dataCategory);
    }

    @Test
    public void testPathResolving12() throws Exception {
        info();
        ClassDefinition obj = XEnumSetDefinition.BClass.INSTANCE.getMetaData();
        FieldDefinition f = FieldGetter.getFieldDefinitionForPathname(obj, "baseXEnum");
        Assert.assertEquals(f, XEnumSetDefinition.meta$$baseXEnum);
        
        ClassDefinition obj2 = ClassDefinition.BClass.INSTANCE.getMetaData();
        
        FieldDefinition f2 = FieldGetter.getFieldDefinitionForPathname(obj2, "fields[2].dataCategory");
        Assert.assertEquals(f2, FieldDefinition.meta$$dataCategory);
    }

}
