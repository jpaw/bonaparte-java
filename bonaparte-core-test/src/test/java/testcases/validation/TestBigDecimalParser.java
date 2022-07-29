package testcases.validation;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;

public class TestBigDecimalParser {

    @Test
    public void testBigDecimal() throws Exception {

        SimpleTestRunner.run(new BDTest(new BigDecimal("3.14010000")), false);
        SimpleTestRunner.run(new BDTest(new BigDecimal("3.14010000000")), false);
        try {
            SimpleTestRunner.run(new BDTest(new BigDecimal("3.140100000003")), false);
        } catch (MessageParserException e) {
            if (e.getErrorCode() != 200017041)  // this is the expected one: (number contains more decimal places than allowed)
                throw e;
        }
    }

}
