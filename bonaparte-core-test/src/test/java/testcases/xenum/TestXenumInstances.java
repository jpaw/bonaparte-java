package testcases.xenum;

import java.util.List;

import org.testng.annotations.Test;

import de.jpaw.enums.XEnumFactory;
import de.jpaw.xenums.init.XenumInitializer;

public class TestXenumInstances {
    @Test
    public void testXenums2() throws Exception {
        XenumInitializer.initializeXenums("de.jpaw.bonaparte.pojos.testXenum");
        XEnumFactory f = XEnumFactory.getFactoryByPQON("testXenum.XColor");
        List l = f.valuesAsList();
        for (Object e : l) {
            System.out.println("e is of class " + e.getClass().getCanonicalName() + " and has value " + e);
        }
    }
}
