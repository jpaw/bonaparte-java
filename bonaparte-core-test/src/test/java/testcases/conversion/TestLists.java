package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillLists;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestLists class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          This is a simple testcase which calls the SimpleTestRunner with a class
 *          consisting of all supported BonaPortable List types.
 */
public class TestLists {

    @Test
    public void testLists() throws Exception {
        SimpleTestRunner.run(FillLists.test1(), false);
    }
}
