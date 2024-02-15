package testcases.conversion;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.coretests.initializers.FillLists;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;
import de.jpaw.bonaparte.testrunner.MultiTestRunner;

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

    @Test
    public void testListsStd() throws Exception {
        MultiTestRunner.serDeserMulti(FillLists.test1(), null);
    }
}
