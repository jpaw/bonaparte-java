package de.jpaw.money.test

import org.testng.annotations.Test
import de.jpaw.money.BonaCurrency
import de.jpaw.money.MonetaryException

/** xtend test cases. To run "as TestNG" from inside Eclipse, find the generated/java folder, and run the Java class there. */

public class TestCurrency {

    def private testISO(String code, int expectDigits, int expectedErrorCode) throws Exception {
        try {
            val gotDecimals = new BonaCurrency(code).decimals
            if (gotDecimals != expectDigits)
                throw new Exception('''Expected «expectDigits» decimals for «code», but got «gotDecimals»''')
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase «code» to throw Exception «expectedErrorCode»''')
        } catch (MonetaryException e) {
            if (e.errorCode != expectedErrorCode)
                throw e
            // else ignore, this is the expected result
        }        
    }

    /** Prove that ISO 4217 codes are accepted without issues, but  */
    @Test
    def public void testInternalISOTable() throws Exception {
        testISO('USD',  2, 0)
        testISO('JPY',  0, 0)
        testISO('TND',  3, 0)
        testISO('ABC', -1, MonetaryException::NOT_AN_ISO4217_CODE)
        testISO('usd', -1, MonetaryException::ILLEGAL_CURRENCY_CODE)
        testISO('US',  -1, MonetaryException::ILLEGAL_CURRENCY_CODE)
    }
    
    def private testCodewithProvidedDecimals(String code, int digits, int expectedErrorCode) throws Exception {
        try {
            val gotDecimals = new BonaCurrency(code, digits).decimals
            if (gotDecimals != digits)
                throw new Exception('''Expected «digits» decimals for «code» as provided, but got «gotDecimals»''')
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase «code» to throw Exception «expectedErrorCode»''')
        } catch (MonetaryException e) {
            if (e.errorCode != expectedErrorCode)
                throw e
            // else ignore, this is the expected result
        }        
    }
    
    /** Prove that ISO 4217 codes scales can be overridden and that with provided precision, virtual currencies are accepted as well. */
    @Test
    def public void testPrecisionOverride() throws Exception {
        testCodewithProvidedDecimals('USD',  6, 0)
        testCodewithProvidedDecimals('JPY',  1, 0)
        testCodewithProvidedDecimals('TND',  2, 0)
        testCodewithProvidedDecimals('ABC',  3, 0)
        testCodewithProvidedDecimals('usd',  2, MonetaryException::ILLEGAL_CURRENCY_CODE)
        testCodewithProvidedDecimals('USD', -1, MonetaryException::ILLEGAL_NUMBER_OF_DECIMALS)
        testCodewithProvidedDecimals('USD',BonaCurrency::MAX_DECIMALS + 1, MonetaryException::ILLEGAL_NUMBER_OF_DECIMALS)
    }    
}