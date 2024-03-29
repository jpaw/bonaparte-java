package de.jpaw.bonaparte.csvTests;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.CSVStyle;
import de.jpaw.bonaparte.pojos.csvTests.Test1withMicroUnits;
import de.jpaw.fixedpoint.types.MicroUnits;

public class CSVDemo {
    private static Test1withMicroUnits t = new Test1withMicroUnits(
      "Hello, world", 42, new BigDecimal("3.14"), LocalDateTime.now(), MicroUnits.valueOf(new BigDecimal("-33555.178")), LocalDate.now(), true, -78653L);

    private static void run2Tests(CSVConfiguration cfg, String formatName, boolean useComposer2) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = useComposer2 ? new CSVComposer2(buffer, cfg) : new CSVComposer(buffer, cfg);
        try {
            cmp.writeRecord(t);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        System.out.print("Format " + formatName + " using composer" + (useComposer2 ? "2" : "1") + " is " + buffer);
    }

    private static void runTest(CSVConfiguration cfg, String formatName) {
        run2Tests(cfg, formatName, false);
        run2Tests(cfg, formatName, true);
    }

    private static void testTag(String tag, String name) {
        runTest(new CSVConfiguration.Builder()
        .forLocale(Locale.forLanguageTag(tag))
        .dateTimeStyle(CSVStyle.MEDIUM, CSVStyle.MEDIUM)
        .usingSeparator("; ")
        .setCustomDayTimeFormats(null, null, null, null, null)
        .build(), name);

    }

    public static void main(String[] args) {
        CSVConfiguration.Builder builder = new CSVConfiguration.Builder();
        builder.usingGrouping(true);

        runTest(builder.build(), "default");
        runTest(builder.forLocale(Locale.GERMANY).build(), "DE");
        runTest(builder.forLocale(Locale.UK).build(), "UK");
        runTest(builder.forLocale(Locale.US).build(), "US");
        runTest(builder
                .forLocale(Locale.GERMANY)
                .dateTimeStyle(CSVStyle.MEDIUM, CSVStyle.MEDIUM)
                .usingSeparator("; ")
                .booleanTokens("WAHR", "FALSCH")  // as used by Excel
                .resetCustomDayTimeFormats()
                .build(), "DE extended");
        testTag("th_TH_TH", "thai");                            // latin numbers
        testTag("th-TH-u-nu-thai", "thai (with BCP47 code)");   // Thai numbers!
        testTag("ar_EG", "Arabic (Egypt)");                     // latin
        runTest(builder.forLocale(new Locale("ar", "EG")).build(), "Arabic EG");    // arabic format, minus behind digits
    }

}
