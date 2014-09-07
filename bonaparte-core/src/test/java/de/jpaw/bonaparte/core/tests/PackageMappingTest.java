package de.jpaw.bonaparte.core.tests;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortableFactory;

public class PackageMappingTest {
    
    @Test
    public void testDefaultPackageMapping() throws Exception {
        assert BonaPortableFactory.mapPackage("myStuff") == null;
        assert BonaPortableFactory.mapPackage("meta.ClassDefinition").equals("de.jpaw.bonaparte.pojos.meta.ClassDefinition");
        assert BonaPortableFactory.mapPackage("money.Amount").equals("de.jpaw.bonaparte.pojos.money.Amount");
    }
    
    @Test
    public void testModifiedPackageMapping() throws Exception {
        assert (BonaPortableFactory.addToPackagePrefixMap("money", null).equals("de.jpaw.bonaparte.pojos.money"));                      // new mapping is "none"
        assert(BonaPortableFactory.addToPackagePrefixMap("meta.special", "somewhereelse") == null);                                     // new mapping for subpackage
        assert BonaPortableFactory.mapPackage("myStuff") == null;
        assert BonaPortableFactory.mapPackage("meta.ClassDefinition").equals("de.jpaw.bonaparte.pojos.meta.ClassDefinition");           // unchanged
        assert BonaPortableFactory.mapPackage("meta.special.Type").equals("somewhereelse.Type");                                       // completely different
        assert BonaPortableFactory.mapPackage("money.Amount") == null;
    }
}
