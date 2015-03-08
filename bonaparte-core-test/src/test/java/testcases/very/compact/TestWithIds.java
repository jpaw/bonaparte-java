package testcases.very.compact;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.pojos.very.compact.withIds.Complex;
import de.jpaw.bonaparte.pojos.very.compact.withIds.Vector;
import de.jpaw.bonaparte.scanner.BClassScanner;
import de.jpaw.util.ByteUtil;

public class TestWithIds {
    @BeforeSuite
    public void registerClasses() {
        BClassScanner.init();
    }

    @Test
    public void testCompactWithIds() throws Exception {
        Complex x = new Complex("AA", 65535);
        Complex yAndZ = new Complex("aa", 170*256+170);

        Vector org = new Vector(x, yAndZ, yAndZ);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(1000, true);
        cbac.writeRecord(org);
        System.out.println("Length with CompactByteArrayComposer (ID) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));
        assert(data.length == 24);  // outer object per PQON, first two complex components as base object, last as repeated.

        // parse the result
        CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        BonaPortable copy = cbap.readRecord();
        assert(copy != null);
        assert(copy instanceof Vector);
        assert(org.equals(copy));
    }

}
