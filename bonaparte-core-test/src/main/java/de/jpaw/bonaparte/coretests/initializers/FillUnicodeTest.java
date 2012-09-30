package de.jpaw.bonaparte.coretests.initializers;

import de.jpaw.bonaparte.pojos.tests1.UnicodeTest;

public class FillUnicodeTest {
    static private final int VIOLIN[] = { 0x1D11E };  // 4 byte UTF-8 sequence
    static private final int UNICODE_MAX[] = { 0x10FFFF };

    static public UnicodeTest test1() {
        UnicodeTest x = new UnicodeTest();
        x.setString1(new String(VIOLIN, 0, 1));
        x.setString2(new String(UNICODE_MAX, 0, 1));
        return x;
    }

}
