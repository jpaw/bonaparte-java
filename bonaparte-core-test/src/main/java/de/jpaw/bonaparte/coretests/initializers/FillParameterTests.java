package de.jpaw.bonaparte.coretests.initializers;

import de.jpaw.bonaparte.pojos.tests1.Parameters;

public class FillParameterTests {

    /**
     * @param args
     */
    static public Parameters test1() {
        Parameters x = new Parameters();
        x.setTestNoTrim    ("   no trim   ");
        x.setTestTrim      ("   no trim   ");
        x.setTestNoTruncate("I am a string which is much too long");
        x.setTestTruncate  ("I am a string which is much too long");
        return x;
    }

}
