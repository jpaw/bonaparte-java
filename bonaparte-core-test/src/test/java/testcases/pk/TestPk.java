package testcases.pk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.pojos.pkTest.ClassA;
import de.jpaw.bonaparte.pojos.pkTest.ClassC;

public class TestPk {

    @Test
    public void testPkInheritance() throws Exception {

        BonaPortableClass<?> keyClass = ClassC.BClass.INSTANCE.getPrimaryKey();
        Assertions.assertNotNull(keyClass);
        Assertions.assertEquals(keyClass, ClassA.BClass.INSTANCE.getPrimaryKey());
    }
}
