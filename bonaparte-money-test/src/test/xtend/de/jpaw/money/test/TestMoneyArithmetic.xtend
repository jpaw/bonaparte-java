package de.jpaw.money.test

import org.testng.annotations.Test
import de.jpaw.money.BonaCurrency
import de.jpaw.money.MonetaryException
import de.jpaw.money.BonaMoney
import static extension de.jpaw.money.BonaMoneyOperators.*

/** xtend test cases. To run "as TestNG" from inside Eclipse, find the generated/java folder, and run the Java class there. */
public class TestMoneyArithmetic {
    def private static testAdd(int expectedErrorCode, BonaMoney a, BonaMoney b) {
        try {
            val x = a + b;
            if (expectedErrorCode != 0)
                throw new Exception('''Expected testcase to throw Exception «expectedErrorCode»''')
            return x
        } catch (MonetaryException e) {
            if (e.errorCode != expectedErrorCode)
                throw e
            // else ignore, this is the expected result
            return a.currency.zero
        }        
    }
    
    @Test
    def public void testOperandsCompatibility() {
        val c1 = new BonaCurrency('USD')
        val c2 = new BonaCurrency('EUR')
        
        val a1a = new BonaMoney(c1, false, 120BD)           // base amount
        val a1b = new BonaMoney(c1, false, 17BD)            // compatible amount
        val a2 = new BonaMoney(c2, false, 18BD)             // incompatible for add/sub, different currency
        val a3 = new BonaMoney(c1, false, true, null, 19BD, 2BD)  // incompatible due to tax
        
        testAdd(0, a1a, a1b)
        testAdd(MonetaryException::INCOMPATIBLE_OPERANDS, a1a, a2)  // incompatible for add/sub, different currency
        testAdd(MonetaryException::INCOMPATIBLE_OPERANDS, a1a, a3)  // incompatible for add/sub, different currency
    }
}