package de.jpaw.bonaparte.coretests.initializers;

import de.jpaw.bonaparte.pojos.tests1.UnicodeTest;

/** Tests some characters outside of BMP0. Here, BE / LE is relevant. */

public class FillUnicodeTest {
    static private final int VIOLIN[] = { 0x1D11E };  // 4 byte UTF-8 sequence
    static private final int UNICODE_MAX[] = { 0x10FFFF };

    static public UnicodeTest test1() {
        UnicodeTest x = new UnicodeTest();
        x.setString1(new String(VIOLIN, 0, 1));         // construct a string from an array of code points
        x.setString2(new String(UNICODE_MAX, 0, 1));    // construct a string from an array of code points
        return x;
    }

}
