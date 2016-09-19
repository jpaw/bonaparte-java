package testcases.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithMap;
import de.jpaw.bonaparte.pojos.jsonTest.TestSimple;

public class JsonMapTest {
    private static final JsonWithMap j = new JsonWithMap(ColorAlnum.RED, new HashMap<String, String>(), 12);

    private static final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithMap\",\"en\":\"R\",\"map\":{\"A\":\"B\"},\"num\":12}\n";

    @Test
    public void runBonaMap() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        j.getMap().put("A", "B");
        String j1 = JsonComposer.toJsonString(j);
        System.out.println("Bonaparte produces " + j1);

        
        Assert.assertEquals(j1, expected1);

        // list related tests: empty list
        List<TestSimple> myList = new ArrayList<TestSimple>(2);
        Assert.assertEquals(JsonComposer.toJsonString(myList), "[]");


//        System.out.println("Bonaparte produces " + JsonComposer.toJsonString(t2));
//        System.out.println("Bonaparte produces " + JsonComposer.toJsonString(myList));
    }
}
