package de.jpaw.money.test

import org.testng.annotations.Test
import de.jpaw.money.BonaCurrency
import de.jpaw.money.MonetaryException
import java.math.BigDecimal

/** xtend test cases. To run "as TestNG" from inside Eclipse, find the generated/java folder, and run the Java class there. */

public class TestCurrency {

    def static public assertion(boolean condition, CharSequence msg) {
        if (!condition)
            throw new Exception(msg.toString)
    }
    
    def private testISO(String code, int expectDigits, int expectedErrorCode) throws Exception {
        try {
            val curr = new BonaCurrency(code)
            if (curr.decimals != expectDigits)
                throw new Exception('''Expected «expectDigits» decimals for «code», but got «curr.decimals»''')
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase «code» to throw Exception «expectedErrorCode»''')
                
            // test the zeros and smallest units as well
            assertion(curr.zero.scale == expectDigits, '''Zero for «code» has incorrect scale: «curr.zero.scale»''')
            assertion(curr.smallestUnit.scale == expectDigits, '''Smallest unit for «code» has incorrect scale: «curr.smallestUnit.scale»''')
            // assert the smallest unit is of correct value
            assertion(curr.smallestUnit.scaleByPowerOfTen(expectDigits).compareTo(BigDecimal::ONE) == 0, '''Smallest unit for «code» does not multiply back to 1''') 
        } catch (MonetaryException e) {
            if (e.errorCode != expectedErrorCode)
                throw e
            // else ignore, this is the expected result
        }        
    }

    /** Prove that ISO 4217 codes are accepted without issues. */
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
            val curr = new BonaCurrency(code, digits)
            if (curr.decimals != digits)
                throw new Exception('''Expected «digits» decimals for «code» as provided, but got «curr.decimals»''')
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase «code» to throw Exception «expectedErrorCode»''')
            // test the zeros and smallest units as well
            assertion(curr.zero.scale == digits, '''Zero for «code» has incorrect scale: «curr.zero.scale»''')
            assertion(curr.smallestUnit.scale == digits, '''Smallest unit for «code» has incorrect scale: «curr.smallestUnit.scale»''')
            // assert the smallest unit is of correct value
            assertion(curr.smallestUnit.scaleByPowerOfTen(digits).compareTo(BigDecimal::ONE) == 0, '''Smallest unit for «code» does not multiply back to 1''') 
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
    
    @Test
    def public void testCurrencyEquals() throws Exception {
        val c1a = new BonaCurrency('USD')
        val c1b = new BonaCurrency('USD', 2)   // same currency, but created with explicit decimals override
        val c2 = new BonaCurrency('USD', 6)     // different number of decimals
        val c3 = new BonaCurrency('EUR', 2)     // different currency code, but same number of decimals
        
        assertion(c1a.equals(c1b), 'Separately created currencies with identical parameters should be the same')
        assertion(!c1a.equals(c2), 'Currencies with different number of decimals should not be regarded as same')
        assertion(!c1a.equals(c3), 'Currencies with different currency codes should not be regarded as same')
    }
}