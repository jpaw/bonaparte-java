package testcases.csv;

import java.io.IOException;
import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FixedWidthComposer;
import de.jpaw.bonaparte.pojos.csvTests.Test1;

public class TestFixedWidth {

    private CSVConfiguration fixedWidthCfg = new CSVConfiguration.Builder().usingSeparator("").usingQuoteCharacter(null).usingZeroPadding(true).build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) {
        StringBuilder buffer = new StringBuilder(200);
        FixedWidthComposer cmp = new FixedWidthComposer(buffer, cfg);
        cmp.setWriteCRs(false);
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
    public void testFixedWidth() throws Exception {
        Test1 t1 = new Test1("Hello", 12, new BigDecimal("3.1"), new LocalDateTime(2013, 04, 01, 23, 55, 0), new LocalDate(2001, 11, 12), true, null, 1234567890123L);

        runTest(fixedWidthCfg,  t1, "Hello     000000012 000000000003.10 20130401235500200111121000001234567890123\n");
        
        CSVConfiguration fixedWidthCfg2 = CSVConfiguration.Builder.from(fixedWidthCfg).booleanTokens("J", "N").setCustomDayTimeFormats("dd.MM.YYYY", "YYYY-MM-dd HH:mm:ss", "YYYY-MM-DD HH:mm:ss SSS").usingZeroPadding(false).build();
        runTest(fixedWidthCfg2, t1, "Hello            12            3.10 2013-04-01 23:55:0012.11.2001J     1234567890123\n");
    }
}
