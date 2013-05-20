package de.jpaw.money.test

import org.testng.annotations.Test
import de.jpaw.money.BonaCurrency
import de.jpaw.money.MonetaryException
import de.jpaw.money.BonaMoney
import java.math.BigDecimal

/** xtend test cases. To run "as TestNG" from inside Eclipse, find the generated/java folder, and run the Java class there. */
public class TestMoney {
    static private val testCurrency = new BonaCurrency('USD')
    static private val zeroAmount = new BonaMoney(testCurrency)
    
    def private static createMoney(int expectedErrorCode, boolean allowRounding, BigDecimal gross, BigDecimal ... tax) {
        try {
            val x = new BonaMoney(testCurrency, allowRounding, true, gross, tax);
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase to throw Exception «expectedErrorCode»''')
            return x
        } catch (MonetaryException e) {
            if (e.errorCode != expectedErrorCode)
                throw e
            // else ignore, this is the expected result
            return zeroAmount
        }        
    }
    
    def public static expectSame(boolean same, BonaMoney a, BonaMoney b) {
        if (a.equals(b) != same)
            throw new Exception('''expected «a» «IF same»==«ELSE»<>«ENDIF» «b»''')       
    }
    
    @Test
    def public void testRounding() {
        createMoney(0, false, null, 100.0BD, 19.0BD)        // OK, can determine gross from net + taxes
        createMoney(0, false, 119.0BD, 100.0BD, 19.0BD)     // OK, all data provided, and gross = net + taxes is correct
        createMoney(MonetaryException::UNDEFINED_AMOUNTS, false,     null)    // neither net nor gross supplied 
        createMoney(MonetaryException::SUM_MISMATCH,      false, 119.01BD,  100.0BD, 19.0BD)    // all parameters supplied, but the sum mismatches
        createMoney(MonetaryException::SIGNS_DIFFER,      false,  10.0BD,      -9BD, 19.0BD)    // tax > gross => net is negative
        createMoney(MonetaryException::ROUNDING_PROBLEM,  false, 119.001BD, 100.0BD, 19.001BD)  // problem because rounding not allowed here
        expectSame(true,                                          // expect rounding to appear at the value with smallest relative error (tax 1 value)
            new BonaMoney(testCurrency, true,  true,    null, 10.004BD, 20.003BD, 10.003BD),
            new BonaMoney(testCurrency, false, true, 40.01BD, 10BD,     20.01BD,  10BD)
        ); 
        expectSame(true,                                                                    // twopenny rounding: up
            new BonaMoney(testCurrency, true,  34.555BD),
            new BonaMoney(testCurrency, false, 34.56BD)
        ); 
        expectSame(true,                                                                    // twopenny rounding: down
            new BonaMoney(testCurrency, true,  34.525BD),
            new BonaMoney(testCurrency, false, 34.52BD)
        ); 
    }
}