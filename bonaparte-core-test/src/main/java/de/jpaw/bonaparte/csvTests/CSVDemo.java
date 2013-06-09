package de.jpaw.bonaparte.csvTests;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.CSVStyle;
import de.jpaw.bonaparte.pojos.csvTests.Test1;

public class CSVDemo {
    private static Test1 t = new Test1("Hello, world", 42, new BigDecimal("3.14"), LocalDateTime.now(), LocalDate.now(), true);

    private static void runTest(CSVConfiguration cfg, String formatName) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, cfg);
        try {
            cmp.writeRecord(t);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        System.out.print("Format " + formatName + " is " + buffer);
    }
    
    public static void main(String[] args) {
        CSVConfiguration.Builder builder = new CSVConfiguration.Builder();

        runTest(builder.build(), "default");
        runTest(builder.forLocale(Locale.GERMANY).build(), "DE");
        runTest(builder.forLocale(Locale.UK).build(), "UK");
        runTest(builder.forLocale(Locale.US).build(), "US");
        runTest(builder
                .forLocale(Locale.GERMANY)
                .dateTimeStyle(CSVStyle.MEDIUM, CSVStyle.MEDIUM)
                .usingSeparator("; ")
                .booleanTokens("WAHR", "FALSCH")  // as used by Excel
                .setCustomCalendarFormat(null)
                .setCustomDayTimeFormats(null, null, null)
                .build(), "DE extended");
    }

}
