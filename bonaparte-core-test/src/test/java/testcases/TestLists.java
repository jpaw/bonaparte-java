package testcases;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.coretests.initializers.FillLists;
import de.jpaw.bonaparte.coretests.util.SimpleTestRunner;

public class TestLists {
	
	@Test
	public void testLists() throws Exception {
		SimpleTestRunner.run(FillLists.test1(), false);
	}
}
