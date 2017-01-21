package testcases.json;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapComposer;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithStringMapOfEnums;

public class JsonEnumStringMapTest {
    private static final Map<String,ColorNum>   t1 = new HashMap<String,ColorNum>();
    private static final Map<String,ColorAlnum> t2 = new HashMap<String,ColorAlnum>();
    static {
        t1.put("a", ColorNum.RED);
        t1.put("b", ColorNum.GREEN);
        t2.put("a", ColorAlnum.RED);
        t2.put("b", ColorAlnum.GREEN);
    }
    private static final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithStringMapOfEnums\",\"cnMap\":{\"a\":0,\"b\":1},\"num1\":42,\"caMap\":{\"a\":\"R\",\"b\":\"G\"},\"num2\":28}";

    @Test
    public void runBonaStringMap() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);
        
        JsonWithStringMapOfEnums jwl = new JsonWithStringMapOfEnums();
        jwl.setNum1(42);
        jwl.setNum2(28);
        jwl.setCnMap(t1);
        jwl.setCaMap(t2);

        // non list related tests
        String j1 = JsonComposer.toJsonString(jwl);
        System.out.println("Bonaparte produces " + j1);
        Assert.assertEquals(j1, expected1 + "\n");

        System.out.println("MapComposer produces " + MapComposer.marshal(jwl));

        String j2 = BonaparteJsonEscaper.asJson(jwl);
        System.out.println("BJE produces  produces " + j2);
        Assert.assertEquals(j2, expected1);
    }
}
