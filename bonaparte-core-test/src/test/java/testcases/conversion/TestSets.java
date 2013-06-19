package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillMaps;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestMaps class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of all supported BonaPortable Map types.
 */
public class TestSets {

    @Test
    public void testSets() throws Exception {
        SimpleTestRunner.run(FillMaps.testSets1(), false);
    }

}
