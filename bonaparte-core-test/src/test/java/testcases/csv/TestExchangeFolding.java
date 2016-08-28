package testcases.csv;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CSVComposer;
import de.jpaw.bonaparte.core.CSVConfiguration;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.pojos.exchange.Ratios;
import de.jpaw.bonaparte.pojos.exchange.RatiosPerDay;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

public class TestExchangeFolding {
    private CSVConfiguration unixPasswdCfg = new CSVConfiguration.Builder().usingSeparator(":").usingQuoteCharacter(null).build();

    private RatiosPerDay setupRpd(BigDecimal factor) {
        RatiosPerDay rpd = new RatiosPerDay(LocalDate.now(), new HashMap<String, BigDecimal>(10));
        rpd.ratioToCurrency.put("EUR", factor.multiply(new BigDecimal("3.14")));
        rpd.ratioToCurrency.put("USD", factor.multiply(new BigDecimal("2.71")));
        rpd.ratioToCurrency.put("GBP", factor.multiply(new BigDecimal("3.33")));
        return rpd;
    }

    private Ratios setup() {
        Ratios r = new Ratios(new HashMap<String, RatiosPerDay>(10));
        r.forSrc.put("XCF", setupRpd(new BigDecimal("1.1")));
        r.forSrc.put("GRD", setupRpd(new BigDecimal("1.2")));
        r.forSrc.put("MST", setupRpd(new BigDecimal("1.3")));
        return r;
    }

    private static void runTest(CSVConfiguration cfg, BonaPortable input, String expectedOutput,
            Map<Class<? extends BonaCustom>, List<String>> map) {
        StringBuilder buffer = new StringBuilder(200);
        CSVComposer cmp = new CSVComposer(buffer, cfg);
        cmp.setWriteCRs(false);
        FoldingComposer<IOException> fld = new FoldingComposer<IOException>(cmp, map, FoldingStrategy.TRY_SUPERCLASS);
        try {
            fld.writeRecord(input);
        } catch (IOException e) {
            // I hate those checked Exceptions which are even outright wrong!
            throw new RuntimeException("Hey, StringBuilder.append threw an IOException!" + e);
        }
        String actualOutput = buffer.toString();
        System.out.println(actualOutput);
        assert(expectedOutput.equals(actualOutput));
    }


    @Test
    public void testMetaDataWithIndex() throws Exception {
        Ratios r = setup();
        List<String> fields = Arrays.asList( "forSrc[GRD].ratioToCurrency[USD]");
        Map<Class<? extends BonaCustom>, List<String>> map = new HashMap<Class<? extends BonaCustom>, List<String>> (10);
        map.put(Ratios.class, fields);
        runTest(unixPasswdCfg, r, "3.252\n",  map);
    }
}
