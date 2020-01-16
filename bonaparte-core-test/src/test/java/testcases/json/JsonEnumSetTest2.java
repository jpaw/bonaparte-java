package testcases.json;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnumSet;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNumSet;
import de.jpaw.bonaparte.pojos.jsonTest.JsonEnumSets;
import de.jpaw.bonaparte.pojos.jsonTest.WrapperForJsonEnumAndList;
import de.jpaw.bonaparte.pojos.jsonTest.WrapperForJsonEnumSets;
import de.jpaw.bonaparte.pojos.jsonTest.XColor;
import de.jpaw.bonaparte.pojos.jsonTest.XColorSet;
import de.jpaw.json.JsonParser;

// Test which serializes enumsets (with token and without) and xenumsets to JSON
// in various formats:
// - ordinal (untokenizable only)
// - token (tokenizable enum and xenum)
// - instance name enum and xenum)
// and also parses the output and compares for equality

public class JsonEnumSetTest2 {

    @Test
    public void runOrdinalToken() throws Exception {
        runTest(true, true, 82);
    }
    @Test
    public void runOrdinalName() throws Exception {
        runTest(true, false, 87);
    }
    @Test
    public void runNameToken() throws Exception {
        runTest(false, true, 106);
    }
    @Test
    public void runNameName() throws Exception {
        runTest(false, false, 111);
    }

    public void runTest(boolean useOrdinals, boolean useTokens, int lengthExpected) throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        JsonEnumSets in = new JsonEnumSets();
        in.setCn(ColorNumSet.ofTokens(ColorNum.RED, ColorNum.GREEN));
        in.setCa(ColorAlnumSet.ofTokens(ColorAlnum.GREEN));
        in.setCx(XColorSet.ofTokens(XColor.forName("BLUE")));

        in.setCn2(ColorNumSet.ofTokens());
        in.setCa2(ColorAlnumSet.ofTokens());
        in.setCx2(XColorSet.ofTokens());

        // non list related tests
        String j1 = JsonComposer.toJsonString(in, useOrdinals, useTokens);
        System.out.println("Bonaparte produces " + j1 + " (length " + j1.length() + ")");
//        String j2 = BonaparteJsonEscaper.asJson(in);
//        System.out.println("BJE produces       " + j2);
        Assert.assertEquals(lengthExpected, j1.length());

        Map<String, Object> intermediate = (new JsonParser(j1, false)).parseObject();
        BonaPortable out = MapParser.allocObject(intermediate, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        out.deserialize(new MapParser(intermediate, false, useOrdinals, useTokens));

        Assert.assertEquals(out, in);

        // run an additional test for the object wrapped in some other object
        WrapperForJsonEnumSets inW = new WrapperForJsonEnumSets(in);
        String jW = JsonComposer.toJsonString(inW, useOrdinals, useTokens);
        System.out.println("Bonaparte produces " + jW + " (length " + jW.length() + ")");

        Map<String, Object> intermediateW = (new JsonParser(jW, false)).parseObject();
        BonaPortable outW = MapParser.allocObject(intermediateW, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        outW.deserialize(new MapParser(intermediateW, false, useOrdinals, useTokens));

        Assert.assertEquals(outW, inW);
    }
}
