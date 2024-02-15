package testcases.very.compact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.pojos.very.compact.Complex;
import de.jpaw.bonaparte.pojos.very.compact.Container;
import de.jpaw.bonaparte.pojos.very.compact.Vector;
import de.jpaw.bonaparte.scanner.BClassScanner;
import de.jpaw.util.ByteUtil;

public class TestComplex {
    @BeforeEach
    public void registerClasses() {
        BClassScanner.init();
    }

    @Test
    public void testCompactComplexNumber() throws Exception {
    	System.out.println("Java version is " + System.getProperty("java.version"));
        Complex x = new Complex(42.0, 24.0);
        Complex yAndZ = new Complex(1, 0);

        Vector org = new Vector(x, yAndZ, yAndZ);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(1000, false);
        cbac.writeRecord(org);
        System.out.println("Length with CompactByteArrayComposer (ID) (expect 38) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));

        // parse the result
        CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        BonaPortable copy = cbap.readRecord();
        assert(copy != null);
        assert(copy instanceof Vector);
        assert(org.equals(copy));
        assert(data.length == 38);  // outer object per PQON, first two complex components as base object, last as repeated.
    }

    @Test
    public void testVeryCompactComplexNumber() throws Exception {
        // same test, but by calling the writeObject with an artificial outer reference, all types are known
        Complex x = new Complex(42.0, 24.0);
        Complex yAndZ = new Complex(1, 0);

        Vector org = new Vector(x, yAndZ, yAndZ);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(1000, false);
        cbac.addField(Container.meta$$outer, org);      // this line is different from the previous test case
        System.out.println("Length with CompactByteArrayComposer (ID) (expect 18) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));

        // parse the result
        CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        BonaPortable copy = cbap.readObject(Container.meta$$outer, Vector.class);
        assert(copy != null);
        assert(copy instanceof Vector);
        assert(org.equals(copy));
        assert(data.length == 18);  // here we expect less than half the size of the previous test case!
    }

    @Test
    public void testVeryCompactComplexNumberWithIds() throws Exception {
        // same test, but by calling the writeObject with an artificial outer reference, all types are known
        Complex x = new Complex(42.0, 24.0);
        Complex yAndZ = new Complex(1, 0);

        Vector org = new Vector(x, yAndZ, yAndZ);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(1000, true);
        cbac.addField(Container.meta$$outer, org);      // this line is different from the previous test case
        System.out.println("Length with CompactByteArrayComposer (ID) (expect 12) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));

        // parse the result
        CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        BonaPortable copy = cbap.readObject(Container.meta$$outer, Vector.class);
        assert(copy != null);
        assert(copy instanceof Vector);
        assert(org.equals(copy));
        assert(data.length == 12);  // here we expect less than a third of the size of the initial test case!
    }
}
