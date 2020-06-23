package testcases.csv;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.pojos.csvTests.UnixPasswd;

public class TestUnixWithQuotes {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUnixWithQuotes.class);

    private CSVConfiguration unixPasswdCfg = new CSVConfiguration.Builder().usingSeparator(":").usingQuoteCharacter('\'').build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, cfg);
        cmp.setWriteCRs(false);
        try {
            cmp.writeRecord(input);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        String actualOutput = buffer.toString();
        LOGGER.info("Output is {} of length {}, expected length = {}", actualOutput, actualOutput.length(), expectedOutput.length());
        assert(expectedOutput.equals(actualOutput));

        // now parse
        StringCSVParser p = new StringCSVParser(cfg, expectedOutput);
        UnixPasswd parsedOne = p.readObject(UnixPasswd.meta$$this, UnixPasswd.class);
        assertEquals(parsedOne, input);
    }

    @Test
    public void testUnixPasswd() throws Exception {
        UnixPasswd pwEntry = new UnixPasswd("root", "x", 0, 0,"System superuser", "/root", "/bin/sh");
        UnixPasswd pwEntry2 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", "/home/jpaw", "/bin/bash");
        UnixPasswd pwEntry3 = new UnixPasswd("jpaw", "x", 1003, 314,"Michael Bischoff", null, null);

        runTest(unixPasswdCfg, pwEntry, "'root':'x':0:0:'System superuser':'/root':'/bin/sh'\n");
        runTest(unixPasswdCfg, pwEntry2, "'jpaw':'x':1003:314:'Michael Bischoff':'/home/jpaw':'/bin/bash'\n");
        runTest(unixPasswdCfg, pwEntry3, "'jpaw':'x':1003:314:'Michael Bischoff'::\n");  // incomplete last field
    }
}
