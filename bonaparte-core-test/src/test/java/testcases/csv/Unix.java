package testcases.csv;

import java.io.IOException;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.pojos.csvTests.UnixPasswd;

public class Unix {

    private CSVConfiguration unixPasswdCfg = new CSVConfiguration.Builder().usingSeparator(":").usingQuoteCharacter(null).build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, cfg);
        try {
            cmp.writeRecord(input);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        String actualOutput = buffer.toString();
        assert(expectedOutput.equals(actualOutput));
    }
    
    @Test
    public void testUnixPasswd() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        UnixPasswd pwEntry2 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", "/home/jpaw", "/bin/bash");
        
        runTest(unixPasswdCfg, pwEntry, "root:x:0:0:System superuser:/root:/bin/sh\n");
        runTest(unixPasswdCfg, pwEntry2, "jpaw:x:1003:314:Michael Bischoff:/home/jpaw:/bin/bash\n");
    }   

}
