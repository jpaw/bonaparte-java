package testcases.csv;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.pojos.csvTests.ScaledInts;
import de.jpaw.bonaparte.pojos.csvTests.Test1;

public class TestCSV {

    private CSVConfiguration cfg1 = new CSVConfiguration.Builder().usingSeparator(";").usingQuoteCharacter(null).usingZeroPadding(false).build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) throws MessageParserException {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer2 cmp = new CSVComposer2(buffer, cfg);
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
        assert(expectedOutput.equals(actualOutput));

        StringCSVParser p = new StringCSVParser(cfg, actualOutput);
        BonaPortable result = p.readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, input.getClass());
        assert(input.equals(result));
    }

    @Test
    public void testCSVWithImplicitScale() throws Exception {
        CSVConfiguration cfg2 = CSVConfiguration.Builder.from(cfg1).removeDecimalPoint(true).build();

        ScaledInts si1 = new ScaledInts(1, 1L, 1, 1L);
        ScaledInts si2 = new ScaledInts(1, 1L, -1, -1L);
        runTest(cfg1, si1, "0.001;0.000001;0.001;0.000001\n");
        runTest(cfg1, si2, "0.001;0.000001;-0.001;-0.000001\n");
        runTest(cfg2, si1, "1;1;1;1\n");
        runTest(cfg2, si2, "1;1;-1;-1\n");
    }


    static private final Test1 testData = new Test1("Hello", -5, new BigDecimal("-3.14"), null, null, false, 12L);

    private void parseCSVAndCompare(String input, BonaPortable output) throws Exception {
        StringCSVParser p = new StringCSVParser(cfg1, input);
        BonaPortable o = p.readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, output.getClass());
        Assert.assertEquals(output, o);
    }

    @Test
    public void testCSVLeadingSigns() throws Exception {
        String input = "Hello;-5;-3.14;;;0;12";
        parseCSVAndCompare(input, testData);
    }

    @Test
    public void testCSVTrailingSigns() throws Exception {
        String input = "Hello;5-;3.14-;;;0;12";
        parseCSVAndCompare(input, testData);
    }

    // test case to show how parsing of CSV with localized decimal point works
    @Test
    public void testCSVNationalDecimalFormat() throws Exception {
        CSVConfiguration cfgDE = CSVConfiguration.Builder.from(cfg1).forLocale(Locale.GERMAN).build();
        String input      = "Hello;-5;-3,14;;;0;12";
        StringCSVParser p = new StringCSVParser(cfgDE, input);
        p.setNationalBigDecimal();  // switch to
        BonaPortable o    = p.readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, testData.getClass());
        Assert.assertEquals(testData, o);
    }
}
