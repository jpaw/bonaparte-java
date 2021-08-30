package testcases.csv;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FixedWidthComposer;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringCSVParser;
import de.jpaw.bonaparte.pojos.csvTests.ScaledInts;
import de.jpaw.bonaparte.pojos.csvTests.Test1;

public class TestFixedWidth {

    private CSVConfiguration fixedWidthCfg1 = new CSVConfiguration.Builder().usingSeparator("").usingQuoteCharacter(null).usingZeroPadding(true).build();

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput) throws MessageParserException {
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
//        System.out.println("Expected  " + expectedOutput);
//        System.out.println("Result is " + actualOutput);
        assert(expectedOutput.equals(actualOutput));

        StringCSVParser p = new StringCSVParser(cfg, actualOutput);
        BonaPortable result = p.readObject(StaticMeta.OUTER_BONAPORTABLE_FOR_CSV, input.getClass());
        assert(input.equals(result));
    }

    @Test
    public void basicTest() throws Exception {
        LocalDateTime.parse("2013-04-01 23:55:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // YYYY won't work!
    }

    @Test
    public void testFixedWidth() throws Exception {
        Test1 t1 = new Test1("Hello", 12, new BigDecimal("3.1"), LocalDateTime.of(2013, 04, 01, 23, 55, 0), LocalDate.of(2001, 11, 12), true, 1234567890123L);

        runTest(fixedWidthCfg1,  t1, "Hello      0000000012000000000003.10 201304012355002001111210000001234567890123\n");

        CSVConfiguration fixedWidthCfg2 = CSVConfiguration.Builder.from(fixedWidthCfg1)
                .booleanTokens("J", "N")
                .setCustomDayFormat("dd.MM.yyyy")
                .setCustomTimeFormats("HH:mm:ss", "HH:mm:ss.SSS")
                .setCustomDayTimeFormats("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS")
                .usingZeroPadding(false).build();
        runTest(fixedWidthCfg2, t1, "Hello              12           3.10 2013-04-01 23:55:0012.11.2001J      1234567890123\n");
    }

    @Test
    public void testFixedWidthWithImplicitScale() throws Exception {
        CSVConfiguration fixedWidthCfg2 = CSVConfiguration.Builder.from(fixedWidthCfg1).usingZeroPadding(false).build();
        CSVConfiguration fixedWidthCfg3 = CSVConfiguration.Builder.from(fixedWidthCfg1).removeDecimalPoint(true).build();

        ScaledInts si1 = new ScaledInts(1, 1L, 1, 1L);
        ScaledInts si2 = new ScaledInts(1, 1L, -1, -1L);
        runTest(fixedWidthCfg1, si1, "00000.001000000000000.000001 00000.001 000000000000.000001\n");
        runTest(fixedWidthCfg1, si2, "00000.001000000000000.000001-00000.001-000000000000.000001\n");
        runTest(fixedWidthCfg2, si1, "     .001            .000001      .001             .000001\n");
        runTest(fixedWidthCfg3, si1, "00000001000000000000000001 00000001 000000000000000001\n");
        runTest(fixedWidthCfg3, si2, "00000001000000000000000001-00000001-000000000000000001\n");
    }
}
