package testcases.json;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapComposer;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithListOfEnums;

public class JsonEnumListTest {
    private static final ColorNum   [] t1 = { ColorNum.RED, ColorNum.GREEN };
    private static final ColorAlnum [] t2 = { ColorAlnum.RED, ColorAlnum.GREEN };

    private static final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithListOfEnums\",\"cnList\":[0,1],\"num1\":42,\"caList\":[\"R\",\"G\"],\"num2\":28}";

    @Test
    public void runBonaList() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        JsonWithListOfEnums jwl = new JsonWithListOfEnums();
        jwl.setNum1(42);
        jwl.setNum2(28);
        jwl.setCnList(Arrays.asList(t1));
        jwl.setCaList(Arrays.asList(t2));

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
