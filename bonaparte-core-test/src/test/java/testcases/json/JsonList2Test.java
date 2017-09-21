package testcases.json;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapComposer;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithList;

public class JsonList2Test {
    private static final Integer [] t1 = { 1, 2, 3 };
    private static final String  [] t2 = { "Hello", "world" };

    private static final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithList\",\"ciList\":[1,2,3],\"num1\":42,\"caList\":[\"Hello\",\"world\"],\"num2\":28}";

    @Test
    public void runBonaList() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        JsonWithList jwl = new JsonWithList();
        jwl.setNum1(42);
        jwl.setNum2(28);
        jwl.setCiList(Arrays.asList(t1));
        jwl.setCaList(Arrays.asList(t2));

        // non list related tests
        String j1 = JsonComposer.toJsonString(jwl);
        Assert.assertEquals(j1, expected1 + "\n");

        System.out.println("MapComposer produces " + MapComposer.marshal(jwl));

        String j2 = BonaparteJsonEscaper.asJson(jwl);
        System.out.println("BJE produces  produces " + j2);
        Assert.assertEquals(j2, expected1);
    }
}
