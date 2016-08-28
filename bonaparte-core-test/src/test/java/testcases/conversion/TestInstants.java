package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillInstants;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestInstants class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of some Instants of variable decimal points.
 */

public class TestInstants {
    @Test
    public void testInstants() throws Exception {
        System.out.println("It is now " + System.currentTimeMillis());
        SimpleTestRunner.run(FillInstants.fillInstants(), false);
    }

}
