package testcases.mfcobol;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.mfcobol.MfcobolParser;
import de.jpaw.bonaparte.pojos.mfcoboltest.BasicTypes;

public class BasicTypesTest {
    
    @Test
    public void testParseBasicTypes() throws Exception {
        final byte[] inputData = "EUR\t\u0001\u0002-123456X".getBytes(StandardCharsets.UTF_8);
        
        final MfcobolParser parser = new MfcobolParser(inputData, 0, inputData.length, StandardCharsets.UTF_8);
        final BasicTypes o = parser.readObject(BasicTypes.meta$$this, BasicTypes.class);
        
        Assertions.assertEquals("EUR",   o.getCurrencyCode(), "currencyCode");
        Assertions.assertEquals(    9,   o.getSingleByte(),   "singleByte");
        Assertions.assertEquals(  258,   o.getTwoBytes(),     "twoBytes");
        Assertions.assertEquals(-123456, o.getDisplayInt(),   "displayInt");
        Assertions.assertEquals("X",     o.getEndMarker(),    "endMarker");
    }
}
