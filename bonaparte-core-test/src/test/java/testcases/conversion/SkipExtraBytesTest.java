package testcases.conversion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.pojos.mytest.Inner;
import de.jpaw.bonaparte.pojos.mytest.Outer;
import de.jpaw.util.ByteUtil;

public class SkipExtraBytesTest {


    static final String data = "\u0012\u000e\u0013mytest.Outer\u0006\u000e\u0013mytest.Inner\u0006\u000e3333\u0006\u000e\u000f66\u0006\u000f\r\n";
    static final String dataLess = "\u0012\u000e\u0013mytest.Outer\u0006\u000e\u0013mytest.Inner\u0006\u000e3333\u0006\u000f66\u0006\u000f\r\n";
    static final String dataMore = "\u0012\u000e\u0013mytest.Outer\u0006\u000e\u0013mytest.Inner\u0006\u000e3333\u0006\u000e\u000e\u000f66\u0006\u000f\r\n";

    @Test
    public void testEncode() throws Exception {
        Outer x = new Outer(new Inner(3333, null), 66);
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(x);
        byte [] result = bac.getBytes();

        System.out.println("Length of buffer is " + result.length);
        System.out.println(ByteUtil.dump(result, 9999));
    }

    @Test
    public void testDecode() throws Exception {
        Outer x = new Outer(new Inner(3333, null), 66);
        ByteArrayParser bap = new ByteArrayParser(data.getBytes(), 0, -1);
        BonaPortable xx = bap.readRecord();
        Assertions.assertEquals(xx, x);
    }

    @Test
    public void testDecodeLess() throws Exception {
        Outer x = new Outer(new Inner(3333, null), 66);
        ByteArrayParser bap = new ByteArrayParser(dataLess.getBytes(), 0, -1);
        BonaPortable xx = bap.readRecord();
        Assertions.assertEquals(xx, x);
    }

    @Test
    public void testDecodeMore() throws Exception {
        Outer x = new Outer(new Inner(3333, null), 66);
        ByteArrayParser bap = new ByteArrayParser(dataMore.getBytes(), 0, -1);
        BonaPortable xx = bap.readRecord();
        Assertions.assertEquals(xx, x);
    }
}
