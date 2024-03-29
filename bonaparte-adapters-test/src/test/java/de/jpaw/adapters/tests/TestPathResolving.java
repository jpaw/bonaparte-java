package de.jpaw.adapters.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortableFactory;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Millis;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigMillis;
import de.jpaw.bonaparte.pojos.adapters.tests.CustomMillis;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.util.FieldGetter;

public class TestPathResolving {
    static {
        BonaPortableFactory.init();
    }

    @Test
    public void testPathResolvingAdapterSingleField() throws Exception {

        ClassDefinition obj = CustomMillis.BClass.INSTANCE.getMetaData();

        FieldDefinition f = FieldGetter.getFieldDefinitionForPathname(obj, "myIntegralMillis", true);
        Assertions.assertEquals(f, Millis.meta$$mantissa);      // the adapter class should be skipped, as it's a single field adapter

        FieldDefinition f2 = FieldGetter.getFieldDefinitionForPathname(obj, "myBigDecimalMillis", true);
        Assertions.assertEquals(f2, BigMillis.meta$$mantissa);
    }

    @Test
    public void testPathNoResolvingAdapterSingleField() throws Exception {

        ClassDefinition obj = CustomMillis.BClass.INSTANCE.getMetaData();

        FieldDefinition f = FieldGetter.getFieldDefinitionForPathname(obj, "myIntegralMillis.mantissa", false);
        Assertions.assertEquals(f, Millis.meta$$mantissa);      // the adapter class should be skipped, as it's a single field adapter

        FieldDefinition f2 = FieldGetter.getFieldDefinitionForPathname(obj, "myBigDecimalMillis.mantissa", false);
        Assertions.assertEquals(f2, BigMillis.meta$$mantissa);
    }
}
