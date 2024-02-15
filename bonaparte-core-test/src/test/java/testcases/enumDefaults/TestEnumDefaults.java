package testcases.enumDefaults;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.enumWithDefaults.ClassWithEnumDefaultsDueToPackageDefaults;
import de.jpaw.bonaparte.pojos.enumWithDefaults.ClassWithoutDefaults;
import de.jpaw.bonaparte.pojos.enumWithDefaults.Color;
import de.jpaw.bonaparte.pojos.enumWithDefaults.Color2;

public class TestEnumDefaults {

    @Test
    public static void testEnumsWithPackageDefaults() {
        ClassWithEnumDefaultsDueToPackageDefaults c1 = new ClassWithEnumDefaultsDueToPackageDefaults();
        assert (c1.getThisShouldBeRed() == Color.RED);
        assert (c1.getThisShouldAlsoBeRed() == Color2.RED);
    }

    @Test
    public static void testEnumsWithoutDefaults() {
        ClassWithoutDefaults c1 = new ClassWithoutDefaults();
        assert (c1.getThisShouldBeRed() == Color.RED);
        assert (c1.getThisShouldAlsoBeRed() == Color2.RED);
        assert (c1.getThisShouldBeNull() == null);
        assert (c1.getThisShouldAlsoBeNull() == null);
    }
}
