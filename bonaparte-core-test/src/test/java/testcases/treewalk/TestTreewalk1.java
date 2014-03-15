package testcases.treewalk;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.StringConverter;
import de.jpaw.bonaparte.pojos.treewalk.DataToFix;
import de.jpaw.util.StringConverterEmptyToNull;
import de.jpaw.util.StringConverterFixer;
import de.jpaw.util.StringConverterTrim;

// test the Trim and empty to null
public class TestTreewalk1 {
	private DataToFix getData () {
		return new DataToFix("  ID1  ", "  ID2  ", "", "  ");
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
    public void testEmptyToNull() throws Exception {
    	checkTestCase(new StringConverterEmptyToNull(), new DataToFix("  ID1  ", "  ID2  ", null, "  "));
    }
    
    @Test
    public void testTrim() throws Exception {
    	checkTestCase(new StringConverterTrim(), new DataToFix("ID1", "ID2", "", ""));
    }
    
    @Test
    public void testTrimSelected() throws Exception {
    	checkTestCase(new StringConverterFixer(false, false, false), new DataToFix("ID1", "  ID2  ", "", "  "));
    }
    @Test
    public void testTrimAll() throws Exception {
    	checkTestCase(new StringConverterFixer(false, false, true), new DataToFix("ID1", "ID2", "", ""));
    }
    @Test
    public void testTrimSelectedAndEmptyToNull() throws Exception {
    	checkTestCase(new StringConverterFixer(true, false, false), new DataToFix("ID1", "  ID2  ", null, "  "));
    }
    @Test
    public void testTrimAllAndEmptyToNull() throws Exception {
    	checkTestCase(new StringConverterFixer(true, false, true), new DataToFix("ID1", "ID2", null, null));
    }
}
