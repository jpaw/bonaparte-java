package testcases.bigDecimal;

import java.math.BigDecimal;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.core.StringBuilderParser;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTest;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestUnsigned;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestWithAutoRounding;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestWithAutoRounding2;
import de.jpaw.bonaparte.pojos.bigdecimal.BDTestWithAutoRoundingAndScaling;
import de.jpaw.util.ByteBuilder;

/** test cases to see if overflow exception are done in the right cases.
 * Each test is done on 4 different BonaPortable classes (using Decimal with different flags, but same precision) and with 3 different parser / composers.
 * In total, 12 tests are done per external test case. */
public class TestBigDecimalOverflows {

    private void checkParsingException(MessageParser<MessageParserException> p, BigDecimal num, BonaPortable testClass, int parseException) throws Exception {
        // first, do the validation test
        try {
            BonaPortable o = p.readRecord();
            if (parseException != 0) {
                // we should have had an exception
                throw new Exception("Expected a parsing exception for class " + testClass.ret$PQON() + " and value " + num.toPlainString()
                        + " using " + p.getClass().getSimpleName());
            }
            // now the result should be the same as the input
            if (!testClass.equals(o))
                throw new Exception("Record successfully parsed, but contents is not the same for class " + testClass.ret$PQON() + " and value " + num.toPlainString());
        } catch (MessageParserException mpe) {
            if (mpe.getErrorCode() != parseException)
                if (parseException == 0)
                    throw new Exception("Parsing exception " + mpe.getErrorCode() + " thrown"
                        + " for class " + testClass.ret$PQON() + " and value " + num.toPlainString()
                        + " using " + p.getClass().getSimpleName());
                else
                    throw new Exception("Expected a parsing exception " + parseException
                        + " for class " + testClass.ret$PQON() + " and value " + num.toPlainString()
                        + ", but got " + mpe.getErrorCode()
                        + " using " + p.getClass().getSimpleName());
        }
    }

    private void checkOverflow(BigDecimal num, BonaPortable testClass, int parseException, int validationException) throws Exception {
        // first, do the validation test
        try {
            testClass.validate();
            if (validationException != 0) {
                // we should have had an exception
                throw new Exception("Expected a validation exception for class " + testClass.ret$PQON() + " and value " + num.toPlainString());
            }
        } catch (ObjectValidationException ve) {
            if (ve.getErrorCode() != validationException)
                if (validationException == 0)
                    throw new Exception("Validation exception " + ve.getErrorCode() + " thrown"
                        + " for class " + testClass.ret$PQON() + " and value " + num.toPlainString());
                else
                    throw new Exception("Expected a validation exception " + validationException
                        + " for class " + testClass.ret$PQON() + " and value " + num.toPlainString()
                        + ", but got " + ve.getErrorCode());
        }

        // run serialization test for ByteArrayComposer
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(testClass);
        ByteArrayParser bap = new ByteArrayParser(bac.getBytes(), 0, -1);
        checkParsingException(bap, num, testClass, parseException);

        // run serialization test for CompactComposer
        CompactByteArrayComposer cbac = new CompactByteArrayComposer(new ByteBuilder(), false);
        cbac.writeRecord(testClass);
        CompactByteArrayParser cbap = new CompactByteArrayParser(cbac.getBuilder().getBytes(), 0, -1);
        checkParsingException(cbap, num, testClass, parseException);

        // run serialization test for StringBuilderComposer
        StringBuilder sb = new StringBuilder();
        StringBuilderComposer sbc = new StringBuilderComposer(sb);
        sbc.writeRecord(testClass);
        StringBuilderParser sbp = new StringBuilderParser(sb, 0, -1);
        checkParsingException(sbp, num, testClass, parseException);
    }

    // test function. inputNumber is the string to test,
    // parseException is 0 if the parsing should be successful, else the expected error code,
    // validationException is 0 if the validation should be successful, else the expected error code
    // The test is run with all 3 test classes
    private void checkOverflow(String inputNumber, int parseException, int validationException) throws Exception {
        BigDecimal ref = new BigDecimal(inputNumber);
        checkOverflow(ref, new BDTest(ref), parseException, validationException);
        checkOverflow(ref, new BDTestWithAutoRounding(ref), parseException, validationException);
        checkOverflow(ref, new BDTestWithAutoRounding2(ref), parseException, validationException);
        checkOverflow(ref, new BDTestWithAutoRoundingAndScaling(ref), parseException, validationException);
    }

    @Test
    public void testOKNumber() throws Exception {
        checkOverflow("138712633456.6262", 0, 0);
    }

    @Test
    public void testNegativeNumber() throws Exception {
        checkOverflow("-138712633456.6262", 0, 0);
    }

    @Test
    public void testOKFractionalNumber() throws Exception {
        checkOverflow("138712633456.62620000000", 0, 0);
    }

    @Test
    public void testTooManyFractionalNumber() throws Exception {
        BigDecimal ref = new BigDecimal("138712633456.6262123213");
        checkOverflow(ref, new BDTest(ref), MessageParserException.TOO_MANY_DECIMALS, ObjectValidationException.TOO_MANY_FRACTIONAL_DIGITS);
    }

    @Test
    public void testTooManyDigitsNumber() throws Exception {
        checkOverflow("1387132633456.626", MessageParserException.TOO_MANY_DIGITS, ObjectValidationException.TOO_MANY_DIGITS);
    }

    @Test
    public void testNegNOKNumber() throws Exception {
        BigDecimal ref = new BigDecimal("-13856.626");
        checkOverflow(ref, new BDTestUnsigned(ref), MessageParserException.SUPERFLUOUS_SIGN, ObjectValidationException.NO_NEGATIVE_ALLOWED);
    }

}
