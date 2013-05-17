package testcases.validation;

import java.math.BigDecimal;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;

public class TestBigDecimalParser {
    
    @Test
    public void testBigDecimal() throws Exception {
        
        SimpleTestRunner.run(new BDTest(new BigDecimal("3.14010000")), false);
        SimpleTestRunner.run(new BDTest(new BigDecimal("3.14010000000")), false);
        SimpleTestRunner.run(new BDTest(new BigDecimal("3.140100000003")), false);
    }

}
