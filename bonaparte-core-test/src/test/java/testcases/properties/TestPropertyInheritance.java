package testcases.properties;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.pt.MyHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouse;
import de.jpaw.bonaparte.pojos.pt2.YetAnotherHouseRepainted;
import de.jpaw.bonaparte.pojos.pt3.AnotherBus;

public class TestPropertyInheritance {

    @Test
    public void testSimpleProperties() throws Exception {
        assert "red".equals(MyHouse.BClass.INSTANCE.getProperty("color"));
        assert MyHouse.BClass.INSTANCE.getProperty("someWeirdStuff") == null;
    }

    @Test
    public void testInheritedProperties() throws Exception {
        assert "red".equals(YetAnotherHouse.BClass.INSTANCE.getProperty("color"));
        assert "green".equals(YetAnotherHouseRepainted.BClass.INSTANCE.getProperty("color"));
        assert AnotherBus.BClass.INSTANCE.getProperty("color") == null;
    }
}
