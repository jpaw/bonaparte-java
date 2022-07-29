package testcases.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.csvTests.Test1;
import de.jpaw.bonaparte.pojos.csvTests.Test2;
import de.jpaw.util.StringSerializer;

public class StringSerializerTest {

    @Test
    public void bidirectionalConversion() {
        // Given
        Test1 test1 = new Test1();
        test1.setString1("One param");

        Test2 test2 = new Test2();
        test2.setDay1(LocalDate.now());
        test2.setDec1(new BigDecimal(1.2d));
        test2.setInt1(42);
        test2.setLongNum(4200l);
        test2.setReally(true);
        test2.setString1("Hello \t\\");
        test2.setTest1(test1);
        test2.setTests1(Arrays.asList(test1, test1));

        StringBuilder builder = new StringBuilder();
        new StringBuilderComposer(builder).writeRecord(test2);

        // When
        String converted = StringSerializer.toString(builder);
        StringBuilder reConverted = StringSerializer.fromString(converted);

        // Then
        assertEquals(builder.toString(), reConverted.toString());
        System.out.println("StringSerializerTest, Converted output: " + converted);
    }
}
