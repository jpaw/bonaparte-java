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
    
    def private createMoney(int expectedErrorCode, boolean allowRounding, BigDecimal gross, BigDecimal net, BigDecimal ... tax) {
        try {
            val x = new BonaMoney(testCurrency, allowRounding, gross, net, tax);
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
    
    def private expectSame(boolean same, BonaMoney a, BonaMoney b) {
        if (a.equals(b) != same)
            throw new Exception('''expected «a» «IF same»==«ELSE»<>«ENDIF» «b»''')       
    }
    
    @Test
    def public testRounding() {
        createMoney(0, false, 100.0BD, null, 19.0BD)
        createMoney(0, false, null, 100.0BD, 19.0BD)
        createMoney(0, false, 119.0BD, 100.0BD, 19.0BD)
        createMoney(MonetaryException::UNDEFINED_AMOUNTS, false,null, null, 19.0BD)
        createMoney(MonetaryException::SUM_MISMATCH, false, 119.01BD, 100.0BD, 19.0BD)
        createMoney(MonetaryException::SIGNS_DIFFER, false, 10.0BD, null, 19.0BD)   // tax > gross => net would be negative
        expectSame(true,
            new BonaMoney(testCurrency, true,     null, 10.004BD, 20.003BD, 10.003BD),
            new BonaMoney(testCurrency, false, 40.01BD, 10BD,     20.01BD,  10BD)
        ); 
    }
}