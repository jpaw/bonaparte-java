package testcases.properties;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.pt.MyHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouseRepainted;
import de.jpaw.bonaparte.pojos.pt3.AnotherBus;

public class TestPropertyInheritance {

    @Test
    public void testSimpleProperties() throws Exception {
        assert "red".equals(MyHouse.BClass.INSTANCE.getClassProperty("color"));
        assert MyHouse.BClass.INSTANCE.getClassProperty("someWeirdStuff") == null;
    }

    @Test
    public void testInheritedProperties() throws Exception {
        assert "red".equals(YetAnotherHouse.BClass.INSTANCE.getClassProperty("color"));
        assert "green".equals(YetAnotherHouseRepainted.BClass.INSTANCE.getClassProperty("color"));
        assert AnotherBus.BClass.INSTANCE.getClassProperty("color") == null;
    }
}
