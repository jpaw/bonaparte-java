package de.jpaw.bonaparte.core.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortableFactory;

public class PackageMappingTest {

    @Test
    public void testDefaultPackageMapping() throws Exception {
        Assertions.assertNull(BonaPortableFactory.mapPackage("myStuff"));
        Assertions.assertEquals("de.jpaw.bonaparte.pojos.meta.ClassDefinition", BonaPortableFactory.mapPackage("meta.ClassDefinition"));
    }

    @Test
    public void testModifiedPackageMapping() throws Exception {
        Assertions.assertEquals("de.jpaw.bonaparte.pojos.api", BonaPortableFactory.addToPackagePrefixMap("api", null));                 // new mapping is "none"
        Assertions.assertNull((BonaPortableFactory.addToPackagePrefixMap("meta.special", "somewhereelse")));                            // new mapping for subpackage
        Assertions.assertNull(BonaPortableFactory.mapPackage("myStuff"));
        Assertions.assertEquals("de.jpaw.bonaparte.pojos.meta.ClassDefinition", BonaPortableFactory.mapPackage("meta.ClassDefinition"));  // unchanged
        Assertions.assertEquals("somewhereelse.Type", BonaPortableFactory.mapPackage("meta.special.Type"));                               // completely different
    }
}
