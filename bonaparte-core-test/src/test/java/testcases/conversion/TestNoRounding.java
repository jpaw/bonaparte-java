package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillNoRounding;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestNoRounding class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Purpose is to test if breaking numbers with fraction works for negative signs as well (no rounding up/down).
 */

public class TestNoRounding {
    
    @Test
    public void testNoRounding() throws Exception {
        SimpleTestRunner.run(FillNoRounding.test1(), false);
    }   
}
