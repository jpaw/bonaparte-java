package testcases.csv;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.pojos.csvTests.WithBigInt;

public class TestQuoteReplacements {

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) throws MessageParserException {
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
//        System.out.println("Expected  " + expectedOutput);
//        System.out.println("Result is " + actualOutput);
        Assertions.assertEquals(actualOutput, expectedOutput);

//        StringCSVParser p = new StringCSVParser(cfg, actualOutput);
//        BonaPortable result = p.readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, input.getClass());
//        Assertions.assertEquals(result, input);
    }

    @Test
    public void testQuoteReplacement() throws Exception {
        CSVConfiguration withSeparatorCfg = new CSVConfiguration.Builder().usingSeparator(",").usingQuoteCharacter(null).quoteReplacement("!!").build();
        CSVConfiguration withQuotesCfg    = new CSVConfiguration.Builder().usingSeparator(",").usingQuoteCharacter('"').quoteReplacement("!!").build();

        WithBigInt data = new WithBigInt("Hi, world", null, "I say \"Hello\"");
        runTest(withSeparatorCfg, data, "Hi!! world,,I say \"Hello\"\n");
        runTest(withQuotesCfg, data, "\"Hi, world\",,\"I say !!Hello!!\"\n");
    }
}
