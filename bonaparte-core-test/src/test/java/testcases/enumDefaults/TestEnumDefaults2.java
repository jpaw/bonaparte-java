package testcases.enumDefaults;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.enumWithDefaults2.ClassWithEnumDefaultsDueToClassDefaults;
import de.jpaw.bonaparte.pojos.enumWithDefaults2.ClassWithoutDefaults;
import de.jpaw.bonaparte.pojos.enumWithDefaults2.Color;
import de.jpaw.bonaparte.pojos.enumWithDefaults2.Color2;

public class TestEnumDefaults2 {


    @Test
    public static void testEnumsWithPackageDefaults() {
        ClassWithEnumDefaultsDueToClassDefaults c1 = new ClassWithEnumDefaultsDueToClassDefaults();
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
