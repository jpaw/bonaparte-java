package testcases.csv;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.EnumAsTokenComposerFilter;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.pojos.csvTests.Color;
import de.jpaw.bonaparte.pojos.csvTests.Test3;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

public class TestEnumFilter {

    private static CSVConfiguration unixPasswdCfg = new CSVConfiguration.Builder().usingSeparator(":").usingQuoteCharacter(null).build();
    private static List<String> fields = Arrays.asList( "color");
    private static Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
    static {
        map.put(BonaPortable.class, fields);
    }
    private static void runTest(BonaPortable input, String expectedOutput, boolean doEnumFilter, boolean doFolding) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, unixPasswdCfg);
        cmp.setWriteCRs(false);

        MessageComposer<IOException> c1 = doEnumFilter ? new EnumAsTokenComposerFilter<IOException>(cmp) : cmp;
        MessageComposer<IOException> c2 = doFolding ? new FoldingComposer<IOException>(c1, map, FoldingStrategy.TRY_SUPERCLASS) : c1;
        try {
            c2.writeRecord(input);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        String actualOutput = buffer.toString();
        System.out.println(actualOutput);
        if (!expectedOutput.equals(actualOutput))
            System.out.println("Got " + actualOutput + " instead of " + expectedOutput);
        assert(expectedOutput.equals(actualOutput));
    }

    @Test
    public void testUnixPasswd() throws Exception {
        Test3 testCase = new Test3("Hello", Color.GREEN);
        runTest(testCase, "Hello:GREEN\n", true, false);
        runTest(testCase, "Hello:#00FF00\n", false, false);
        runTest(testCase, "#00FF00\n", false, true);
        runTest(testCase, "Hello:GREEN\n", true, false);
        runTest(testCase, "GREEN\n", true, true);
    }

}
