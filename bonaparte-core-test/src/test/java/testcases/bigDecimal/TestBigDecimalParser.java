package testcases.bigDecimal;

import java.math.BigDecimal;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestWithAutoRounding;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestWithAutoRoundingAndScaling;

public class TestBigDecimalParser {
    
    @Test
    public void testBigDecimalByteArrayRd0() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughByteArray(new BDTest(new BigDecimal("3.1")));      // fewer digits than provided
        assert(dst instanceof BDTest);
        assert(((BDTest)(dst)).getAmount().scale() == 1);   // accepted as is
    }
    
    @Test
    public void testBigDecimalByteArrayRd1() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughByteArray(new BDTestWithAutoRounding(new BigDecimal("3.14010067878")));
        assert(dst instanceof BDTestWithAutoRounding);
        assert(((BDTestWithAutoRounding)(dst)).getAmount().scale() == 6);   // scale & round in case of extended precision
    }
    
    @Test
    public void testBigDecimalByteArrayRd2() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughByteArray(new BDTestWithAutoRounding(new BigDecimal("3.14")));
        assert(dst instanceof BDTestWithAutoRounding);
        assert(((BDTestWithAutoRounding)(dst)).getAmount().scale() == 2);  // no scale in case of lower precision
    }

    @Test
    public void testBigDecimalByteArrayRdScale() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughByteArray(new BDTestWithAutoRoundingAndScaling(new BigDecimal("3.14")));
        assert(dst instanceof BDTestWithAutoRoundingAndScaling);
        assert(((BDTestWithAutoRoundingAndScaling)(dst)).getAmount().scale() == 6);  // DOES scale in case of lower precision
    }

    @Test
    public void testBigDecimalStringBuilderRd1() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughStringBuilder(new BDTestWithAutoRounding(new BigDecimal("3.14010067878")));
        assert(dst instanceof BDTestWithAutoRounding);
        assert(((BDTestWithAutoRounding)(dst)).getAmount().scale() == 6);   // scale & round in case of extended precision
    }
    
    @Test
    public void testBigDecimalStringBuilderRd2() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughStringBuilder(new BDTestWithAutoRounding(new BigDecimal("3.14")));
        assert(dst instanceof BDTestWithAutoRounding);
        assert(((BDTestWithAutoRounding)(dst)).getAmount().scale() == 2);  // no scale in case of lower precision
    }

    @Test
    public void testBigDecimalStringBuilderRdScale() throws Exception {
        BonaPortable dst = SimpleTestRunner.runThroughStringBuilder(new BDTestWithAutoRoundingAndScaling(new BigDecimal("3.14")));
        assert(dst instanceof BDTestWithAutoRoundingAndScaling);
        assert(((BDTestWithAutoRoundingAndScaling)(dst)).getAmount().scale() == 6);  // DOES scale in case of lower precision
    }

}
