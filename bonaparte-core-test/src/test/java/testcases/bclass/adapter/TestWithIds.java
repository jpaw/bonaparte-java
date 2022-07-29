package testcases.bclass.adapter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.pojos.bclass.adapters.Container;
import de.jpaw.bonaparte.pojos.bclass.adapters.Target;
import de.jpaw.bonaparte.scanner.BClassScanner;
import de.jpaw.util.ByteUtil;

public class TestWithIds {
    @BeforeAll
    public static void registerClasses() {
        BClassScanner.init();
    }

    @Test
    public void testCompactWithIds() throws Exception {
        Container x = new Container(Target.BClass.INSTANCE);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer(1000, true);
        cbac.writeRecord(x);
        System.out.println("Length with CompactByteArrayComposer (ID) is " + cbac.getBuilder().length());

        // dump the bytes
        byte [] data = cbac.getBuilder().getBytes();
        System.out.println(ByteUtil.dump(data, 100));
        assert(data.length == 13);  // outer object per PQON, first two complex components as base object, last as repeated.

        // parse the result
        CompactByteArrayParser cbap = new CompactByteArrayParser(data, 0, data.length);
        BonaPortable copy = cbap.readRecord();
        assert(copy != null);
        assert(copy instanceof Container);
        Container dst = (Container) copy;
        assert(dst.getMyContainer() != null);
        BonaPortableClass<?> bclass = dst.getMyContainer();
        BonaPortable zzz = bclass.newInstance();
        assert(zzz instanceof Target);
    }
}
