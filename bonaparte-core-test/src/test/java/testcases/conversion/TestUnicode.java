package testcases.conversion;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillUnicodeTest;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

/**
 * The TestUnicode class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Purpose is to test codepoints which do not fit into a single Java char.
 */

public class TestUnicode {
	
	@Test
	public void testUnicode() throws Exception {
		SimpleTestRunner.run(FillUnicodeTest.test1(), false);
	}	
}
