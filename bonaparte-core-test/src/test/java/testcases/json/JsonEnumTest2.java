package testcases.json;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonEnumAndList;
import de.jpaw.bonaparte.pojos.jsonTest.WrapperForJsonEnumAndList;
import de.jpaw.bonaparte.pojos.jsonTest.XColor;
import de.jpaw.json.JsonParser;

// Test which serializes enums (with token and without) and xenums to JSON
// in various formats:
// - ordinal (untokenizable only)
// - token (tokenizable enum and xenum)
// - instance name enum and xenum)
// and also parses the output and compares for equality

public class JsonEnumTest2 {

    @Test
    public void runOrdinalToken() throws Exception {
        runTest(true, true, 62);
    }
    @Test
    public void runOrdinalName() throws Exception {
        runTest(true, false, 69);
    }
    @Test
    public void runNameToken() throws Exception {
        runTest(false, true, 66);
    }
    @Test
    public void runNameName() throws Exception {
        runTest(false, false, 73);
    }

    public void runTest(boolean useOrdinals, boolean useTokens, int lengthExpected) throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        JsonEnumAndList in = new JsonEnumAndList(ColorNum.RED, ColorAlnum.GREEN, XColor.forName("BLUE"), null);

        // non list related tests
        String j1 = JsonComposer.toJsonString(in, useOrdinals, useTokens);
        System.out.println("Bonaparte produces " + j1 + " (length " + j1.length() + ")");
//        String j2 = BonaparteJsonEscaper.asJson(in);
//        System.out.println("BJE produces       " + j2);
        Assertions.assertEquals(lengthExpected, j1.length());

        Map<String, Object> intermediate = (new JsonParser(j1, false)).parseObject();
        BonaPortable out = MapParser.allocObject(intermediate, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        out.deserialize(new MapParser(intermediate, false, useOrdinals, useTokens));

        Assertions.assertEquals(out, in);

        // run an additional test for the object wrapped in some other object
        WrapperForJsonEnumAndList inW = new WrapperForJsonEnumAndList(in);
        String jW = JsonComposer.toJsonString(inW, useOrdinals, useTokens);
        System.out.println("Bonaparte produces " + jW + " (length " + jW.length() + ")");

        Map<String, Object> intermediateW = (new JsonParser(jW, false)).parseObject();
        BonaPortable outW = MapParser.allocObject(intermediateW, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        outW.deserialize(new MapParser(intermediateW, false, useOrdinals, useTokens));

        Assertions.assertEquals(outW, inW);
    }
}
