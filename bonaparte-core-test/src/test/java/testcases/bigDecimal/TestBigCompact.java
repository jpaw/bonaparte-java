package testcases.bigDecimal;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.pojos.tests1.Longtest;
import de.jpaw.bonaparte.pojos.tests1.TestBigDecimal;
import de.jpaw.bonaparte.pojos.tests1.TestBigInteger;

public class TestBigCompact {

    private void runTest(BigInteger n, int run) throws Exception {
        TestBigInteger tbigi = new TestBigInteger(n);
        CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeObject(tbigi);
        cbac.writeObject(new Longtest(7824687777326L));
        byte [] result = cbac.getBytes();

        CompactByteArrayParser cbap = new CompactByteArrayParser(result, 0, -1);
        TestBigInteger rbigi = cbap.readObject(TestBigInteger.meta$$this, TestBigInteger.class);
        Longtest rlngt = cbap.readObject(Longtest.meta$$this, Longtest.class);
        Assertions.assertEquals(rlngt.getL(), 7824687777326L);
        Assertions.assertTrue(tbigi.equals(rbigi));
        //System.out.println("Byte len is " + result.length);

        // test the fractional ones, unless we exceed the mantissa size
        if (run >= 20)
            return;
        for (int i = -4; i <= 9; ++i) {
            if (i >= 0 || run <= 11) {
                // System.out.println("Run " + run + ", scale " + i);
                BigDecimal org = new BigDecimal(n, i);
                TestBigDecimal tbigd = new TestBigDecimal(org);
                cbac.reset();
                cbac.writeObject(tbigd);
                cbac.writeObject(new Longtest(7824687777326L));
                result = cbac.getBytes();
                cbap = new CompactByteArrayParser(result, 0, -1);
                TestBigDecimal rbigd = cbap.readObject(TestBigDecimal.meta$$this, TestBigDecimal.class);
                rlngt = cbap.readObject(Longtest.meta$$this, Longtest.class);
                Assertions.assertEquals(rlngt.getL(), 7824687777326L);
                Assertions.assertTrue(org.compareTo(rbigd.getBigdecnum()) == 0);
            }
        }
    }


    @Test
    public void testBigIntegers() throws Exception {
        BigInteger n = BigInteger.ZERO;
        BigInteger one = BigInteger.valueOf(1);
        BigInteger three = BigInteger.valueOf(3);

        for (int i = 0; i < 63; ++i) {
            runTest(n, i);
            runTest(n.negate(), i);
            n = n.multiply(three).add(one);
        }
    }
}
