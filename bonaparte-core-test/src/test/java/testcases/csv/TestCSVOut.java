package testcases.csv;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.CSVComposer2;
import de.jpaw.bonaparte.core.CSVComposer3;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.pojos.csvTests.Number6;
import de.jpaw.bonaparte.pojos.csvTests.WithBigInt;

public class TestCSVOut {
    private CSVConfiguration cfg = new CSVConfiguration.Builder().usingSeparator(";").usingQuoteCharacter(null).usingZeroPadding(false).build();

    private void run(BigDecimal number, String expected2, String expected3) throws Exception {
        StringBuilder buff = new StringBuilder(100);
        Number6 data = new Number6(number);
        data.freeze();

        CSVComposer2 p2 = new CSVComposer2(buff, cfg);
        p2.writeObject(data);
        Assertions.assertEquals(buff.toString(), expected2);

        buff.setLength(0);
        CSVComposer3 p3 = new CSVComposer3(buff, cfg);
        p3.writeObject(data);
        Assertions.assertEquals(buff.toString(), expected3);
    }

    @Test
    public void testCSVAnyScale() throws Exception {
        run(new BigDecimal(0), "0", "0.000000");
        run(new BigDecimal(0.25), "0.25", "0.250000");
        run(new BigDecimal("7.888120000"), "7.888120000", "7.888120");
    }

    @Test
    public void testCSVBigInteger() throws Exception {
        final WithBigInt data = new WithBigInt("hello", BigInteger.valueOf(14), "world");
        StringBuilder buff = new StringBuilder(100);
        CSVComposer2 p2 = new CSVComposer2(buff, cfg);
        p2.writeObject(data);
        Assertions.assertEquals(buff.toString(), "hello;14;world");
    }

    @Test
    public void testCSVBigIntegerNull() throws Exception {
       final WithBigInt data = new WithBigInt("hello", null, "world");
        StringBuilder buff = new StringBuilder(100);
        CSVComposer2 p2 = new CSVComposer2(buff, cfg);
        p2.writeObject(data);
        Assertions.assertEquals(buff.toString(), "hello;;world");
    }
}
