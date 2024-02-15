package testcases.xenum;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.pojos.csvTests.Color;
import de.jpaw.bonaparte.pojos.testXenum.XColor;
import de.jpaw.enums.TokenizableEnum;
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

    @Test
    public void testXenumsInstance() throws Exception {
        Color c = Color.RED;
        XColor cc = XColor.of(c);
        Assertions.assertTrue(c instanceof TokenizableEnum, "Color should be a TokenizableEnum");
        // Assertions.assertTrue(cc instanceof TokenizableEnum, "XColor should be a TokenizableEnum");
    }
}
