package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.tests1.TestSignedInteger;

public class TestSignedIntegers {
    
    TestSignedInteger fillSigned() {
        TestSignedInteger s = new TestSignedInteger();
        s.setSi(-2000111333);
        s.setSl(-7935107398328647978L);
        return s;
    }
    
    @Test
    public void testSignedIntegers() throws Exception {
        SimpleTestRunner.run(fillSigned(), false);
    }
}
