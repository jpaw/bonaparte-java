package testcases.very.compact;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.pojos.fixedPointTest.Child;
import de.jpaw.bonaparte.pojos.fixedPointTest.Parent;
import de.jpaw.bonaparte.scanner.BClassScanner;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.util.ByteUtil;

public class TestFixedPoint {
    @BeforeEach
    public void registerClasses() {
        BClassScanner.init();
    }

    @Test
    public void testCompactFixedPointNumberWithChildObject() throws Exception {
        testCompactFixedPointNumber(false, 67);
    }

    @Test
    public void testCompactFixedPointNumberWithMap() throws Exception {
        testCompactFixedPointNumber(true, 77);
    }

    public void testCompactFixedPointNumber(final boolean useMaps, final int length) throws Exception {
        final Child child = new Child(12, MicroUnits.valueOf("3.14"));
        final Map<String, Object> map = new HashMap<>();
        map.put("A", MicroUnits.valueOf("2.71828"));
        map.put("B", child);
        final Parent parent = new Parent(17, map);

        final CompactByteArrayComposer cbac = new CompactByteArrayComposer(useMaps);
        cbac.writeRecord(parent);
        System.out.println("Length with CompactByteArrayComposer (ID) (expect 67 or 77) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));

        // parse the result
        final CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        final BonaPortable copy = cbap.readRecord();
        assert(copy != null);
        assert(copy instanceof Parent);
        if (copy instanceof Parent p) {
            final Object a = p.getMap().get("A");
            System.out.println("Copy of A is " + a);
            assert (a instanceof BigDecimal);
            final Object copyOfChild = p.getMap().get("B");
            System.out.println("Copy of child is of class " + (copyOfChild == null ? "null" : copyOfChild.getClass().getCanonicalName()));
            if (useMaps) {
                assert (copyOfChild instanceof HashMap);
                final Map<String, Object> mapCopy = (Map<String, Object>) copyOfChild;
                assert (mapCopy.get("num") instanceof BigDecimal); // type of MicroUnits is converted to BigDecimal because no type info in the map
            } else {
                assert(copyOfChild instanceof Child);
                if (copyOfChild instanceof Child c) {
                    assert(c.getNum().equals(child.getNum()));
                    System.out.println("worked!");
                }
                assert(copyOfChild.equals(child));  // type of MicroUnits is preserved
            }
        }
        assert(data.length == length);
    }
}
