package de.jpaw.bonaparte.adapters.datetime.test;

import java.time.Instant;
import java.time.LocalDate;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.pojos.adapters.datetime.test.UsingJava8Types;
import de.jpaw.bonaparte.testrunner.MultiTestRunner;
import de.jpaw.util.StringSerializer;

public class Java8TypesTest {

    @Test
    public void testJava8Conversion() throws Exception {
        long millis = 98794375943L;
        // Instant.now().truncatedTo(Sec.)
        UsingJava8Types myData = new UsingJava8Types(Instant.ofEpochMilli(millis), LocalDate.of(2010, 12, 31));

        String expectedResult = StringSerializer.fromString(
                "\\R\\N\\Sadapters.datetime.test.UsingJava8Types\\F\\N98794375.943\\F20101231\\F\\O\\J").toString();
        MultiTestRunner.serDeserMulti(myData, expectedResult);
    }
}
