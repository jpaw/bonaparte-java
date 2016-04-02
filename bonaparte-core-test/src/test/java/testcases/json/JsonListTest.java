package testcases.json;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.pojos.jsonTest.TestSimple;

public class JsonListTest {
    private static final TestSimple t1 = new TestSimple(12, "hello");
    private static final TestSimple t2 = new TestSimple(99, null);

    private static final String expected1 = "{\"@PQON\":\"jsonTest.TestSimple\",\"num\":12,\"text\":\"hello\"}\n";
    private static final String expected2 = "{\"@PQON\":\"jsonTest.TestSimple\",\"num\":99}\n";

    @Test
    public void runBonaList() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);

        // non list related tests
        String j1 = JsonComposer.toJsonString(t1);
        String j2 = JsonComposer.toJsonString(t2);
        Assert.assertEquals(j1, expected1);
        Assert.assertEquals(j2, expected2);

        // list related tests: empty list
        List<TestSimple> myList = new ArrayList<TestSimple>(2);
        Assert.assertEquals(JsonComposer.toJsonString(myList), "[]");

        // list related tests: one element list
        myList.add(t1);
        Assert.assertEquals(JsonComposer.toJsonString(myList), "[" + expected1 + "]");

        // list related tests: two element list (separator expected)
        myList.add(t2);
        Assert.assertEquals(JsonComposer.toJsonString(myList), "[" + expected1 + "," + expected2 + "]");

//        System.out.println("Bonaparte produces " + j1);
//        System.out.println("Bonaparte produces " + JsonComposer.toJsonString(t2));
//        System.out.println("Bonaparte produces " + JsonComposer.toJsonString(myList));
    }
}
