package de.jpaw.bonaparte.core.tests;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.pojos.meta.BundleInformation;
import de.jpaw.bonaparte.util.FieldGetter;

public class ReflectionTest {
    @Test
    public void testReflection() throws Exception {
        BonaPortableClass<?> bclass = FieldGetter.getBClass(BundleInformation.class);
        Assertions.assertEquals(bclass,  BundleInformation.BClass.INSTANCE);
    }

    @Test
    public void testReflectionNegative() throws Exception {
        try {
            FieldGetter.getBClass(UUID.class);
            throw new Exception("Expected an exception here");
        } catch (IllegalArgumentException e) {
            // expected, OK!
        }
    }
}
