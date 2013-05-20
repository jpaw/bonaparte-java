package de.jpaw.money.test

import org.testng.annotations.Test
import de.jpaw.money.BonaCurrency

/** xtend test cases. To run "as TestNG" from inside Eclipse, find the generated/java folder, and run the Java class there. */

public class TestRoundingDistribution {


    /** Verify basic rounding distribution. More testcases can be found in the BonaMoney tests (its constructor calls this method as a subroutine). */
    @Test
    def public void testRounding() throws Exception {
        val myCurrency = new BonaCurrency('USD')
        myCurrency.roundWithErrorDistribution(#[10.556BD, 5.223BD, 5.333BD])
   }
    
   
}