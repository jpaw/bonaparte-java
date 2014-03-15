package testcases.treewalk;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.StringConverter;
import de.jpaw.bonaparte.pojos.treewalk.DataToFix;
import de.jpaw.util.StringConverterFixer;

// test the Truncate option in conjunction with Trim
public class TestTreewalkTrunc {
	private DataToFix getData () {
		return new DataToFix("  ID1toolong  ", "  ID2alsotoolong  ", null, null);
	}
	
	private void checkTestCase(StringConverter converter, DataToFix expectedOutcome) {
		DataToFix data = getData();
		// check that data is NOT what we want before the conversion
		assert(!data.equals(expectedOutcome));
		// check that the converted data IS what we want after the conversion
		data.treeWalkString(converter);
		assert(data.equals(expectedOutcome));
	}
	
    @Test
    public void testTruncateSelected() throws Exception {
    	checkTestCase(new StringConverterFixer(false, false, false), new DataToFix("ID1toolong", "  ID2als", null, null));
    }
    @Test
    public void testTruncateAll() throws Exception {
    	checkTestCase(new StringConverterFixer(false, true,  false), new DataToFix("ID1toolo", "  ID2als", null, null));
    }
    @Test
    public void testTrimAllAndTruncateAll() throws Exception {
    	checkTestCase(new StringConverterFixer(false, true,  true), new DataToFix("ID1toolo", "ID2alsot", null, null));
    }
    @Test
    public void testTrimAllTruncateSelected() throws Exception {
    	checkTestCase(new StringConverterFixer(false, false, true), new DataToFix("ID1toolong", "ID2alsot", null, null));
    }
}
