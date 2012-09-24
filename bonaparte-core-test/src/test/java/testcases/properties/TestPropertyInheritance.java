package testcases.properties;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.pt.MyHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouseRepainted;
import de.jpaw.bonaparte.pojos.pt3.AnotherBus;

public class TestPropertyInheritance {
	
	@Test
	public void testSimpleProperties() throws Exception {
		assert "red".equals(MyHouse.class$Property("color"));
		assert MyHouse.class$Property("someWeirdStuff") == null;
	}
	
	@Test
	public void testInheritedProperties() throws Exception {
		assert "red".equals(YetAnotherHouse.class$Property("color"));
		assert "green".equals(YetAnotherHouseRepainted.class$Property("color"));
		assert AnotherBus.class$Property("color") == null;
	}
}
