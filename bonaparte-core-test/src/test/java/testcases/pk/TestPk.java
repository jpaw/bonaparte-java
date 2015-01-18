package testcases.pk;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.pojos.pkTest.ClassA;
import de.jpaw.bonaparte.pojos.pkTest.ClassC;

public class TestPk {

    @Test
    public void testPkInheritance() throws Exception {
        
        BonaPortableClass<?> keyClass = ClassC.BClass.INSTANCE.getPrimaryKey();
        Assert.assertNotNull(keyClass);
        Assert.assertEquals(keyClass, ClassA.BClass.INSTANCE.getPrimaryKey());
    }
}
