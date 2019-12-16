package testcases.json;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonEnumAndList;
import de.jpaw.bonaparte.pojos.jsonTest.XColor;
import de.jpaw.json.JsonParser;

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
        Assert.assertEquals(lengthExpected, j1.length());
        
        Map<String, Object> intermediate = (new JsonParser(j1, false)).parseObject();
        BonaPortable out = MapParser.allocObject(intermediate, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        out.deserialize(new MapParser(intermediate, false, useOrdinals, useTokens));
        
        Assert.assertEquals(out, in);
    }
}
