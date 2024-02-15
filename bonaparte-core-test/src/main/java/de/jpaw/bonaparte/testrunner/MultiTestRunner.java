package de.jpaw.bonaparte.testrunner;

import org.junit.jupiter.api.Assertions;

import de.jpaw.bonaparte.core.BonaPortable;

/** Runs multiple composers / parsers. */
public class MultiTestRunner {

    public static void serDeserMulti(BonaPortable src, String expectedAsString) throws Exception {
        byte [] expectedAsBytes = expectedAsString != null ? expectedAsString.getBytes("UTF-8") : null;

        byte [] gotBA = new ByteArrayTestRunner().serDeser(src, expectedAsBytes);
        String gotSB = new StringBuilderTestRunner().serDeser(src, expectedAsString);

        // both results must match, they're the same story
        byte [] sbAsBa = gotSB.getBytes("UTF-8");
        Assertions.assertArrayEquals(gotBA, sbAsBa);

        byte [] gotCB = new CompactByteArrayTestRunner().serDeser(src, null);
        byte [] gotC = new CompactTestRunner().serDeser(src, gotCB);
        Assertions.assertArrayEquals(gotCB, gotC);

        byte [] gotExt = new ExternalizableTestRunner().serDeser(src, null);

        System.out.println("Lengths are " + gotBA.length + " ASCII, " + gotCB.length + " compact, " + gotExt.length + " externalized");
    }
}
