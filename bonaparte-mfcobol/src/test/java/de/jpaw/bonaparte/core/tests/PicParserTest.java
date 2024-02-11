package de.jpaw.bonaparte.core.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.mfcobol.PicNumeric;
import de.jpaw.bonaparte.mfcobol.PicSignType;
import de.jpaw.bonaparte.mfcobol.PicStorageType;

public class PicParserTest {

    private void test(final String picString, final int expectedIntegralDigits, final int expectedFractionalDigits, final boolean expectedExplicitDecimalPoint, final PicSignType expectedSign, final PicStorageType expectedStorage) {
        final PicNumeric pic = PicNumeric.forPic(picString, picString, null);
        Assertions.assertEquals(expectedIntegralDigits,       pic.integralDigits(),       "Integral digit mismatch for " + picString);
        Assertions.assertEquals(expectedFractionalDigits,     pic.fractionalDigits(),     "Fractional digit mismatch for " + picString);
        Assertions.assertEquals(expectedExplicitDecimalPoint, pic.explicitDecimalPoint(), "Decimal point mismatch for " + picString);
        Assertions.assertEquals(expectedSign,                 pic.sign(),                 "Type of sign mismatch for " + picString);
        Assertions.assertEquals(expectedStorage,              pic.storage(),              "Storage type mismatch for " + picString);
    }

    @Test
    public void testPics() throws Exception {
        test("99",                   2, 0, false, PicSignType.UNSIGNED, PicStorageType.DISPLAY);
        test("S99",                  2, 0, false, PicSignType.IMPLICIT, PicStorageType.DISPLAY);
        test("9(7)",                 7, 0, false, PicSignType.UNSIGNED, PicStorageType.DISPLAY);
        test("99",                   2, 0, false, PicSignType.UNSIGNED, PicStorageType.DISPLAY);
        test("S9(12)",              12, 0, false, PicSignType.IMPLICIT, PicStorageType.DISPLAY);
        test("S9(12)V",             12, 0, false, PicSignType.IMPLICIT, PicStorageType.DISPLAY);
        test("S9(12)V99",           12, 2, false, PicSignType.IMPLICIT, PicStorageType.DISPLAY);
        test("99 COMP",              2, 0, false, PicSignType.UNSIGNED, PicStorageType.BINARY);
        test("S99 COMP",             2, 0, false, PicSignType.IMPLICIT, PicStorageType.BINARY);
        test("9(7) COMP",            7, 0, false, PicSignType.UNSIGNED, PicStorageType.BINARY);
        test("S9(12) COMP",         12, 0, false, PicSignType.IMPLICIT, PicStorageType.BINARY);
        test("S9(12)V     COMP",    12, 0, false, PicSignType.IMPLICIT, PicStorageType.BINARY);
        test("S9(12)V99 COMP",      12, 2, false, PicSignType.IMPLICIT, PicStorageType.BINARY);
        test("S9(12)V99  COMP",     12, 2, false, PicSignType.IMPLICIT, PicStorageType.BINARY);
        test("S9(12)V99  COMP-3",   12, 2, false, PicSignType.IMPLICIT, PicStorageType.PACKED_DECIMAL);
        test("S9(12)V99(16)-  COMP-3", 12, 17, false, PicSignType.IMPLICIT, PicStorageType.PACKED_DECIMAL);
    }
}
