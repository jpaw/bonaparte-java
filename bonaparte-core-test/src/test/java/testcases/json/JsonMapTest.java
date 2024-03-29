package testcases.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.Json2WithMap;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithIntegerMap;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithLongMap;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithMap;
import de.jpaw.bonaparte.pojos.jsonTest.JsonWithMapOfObjects;
import de.jpaw.bonaparte.pojos.jsonTest.TestObj;
import de.jpaw.bonaparte.pojos.jsonTest.TestSimple;

public class JsonMapTest {

    @Test
    public void runBonaList() throws Exception {
        JsonComposer.setDefaultWriteCRs(false);
        // list related tests: empty list
        List<TestSimple> myList = new ArrayList<TestSimple>(2);
        Assertions.assertEquals(JsonComposer.toJsonString(myList), "[]");
    }

    @Test
    public void runBonaMap() throws Exception {
        final JsonWithMap j = new JsonWithMap(ColorAlnum.RED, new HashMap<String, String>(), 12);
        final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithMap\",\"en\":\"R\",\"map\":{\"A\":\"B\"},\"num\":12}\n";
        JsonComposer.setDefaultWriteCRs(false);

        j.getMap().put("A", "B");
        String j1 = JsonComposer.toJsonString(j);
        System.out.println("Bonaparte produces " + j1);
        Assertions.assertEquals(j1, expected1);
    }

    @Test
    public void runBonaMap2() throws Exception {
        final Json2WithMap j = new Json2WithMap(ColorAlnum.RED, new HashMap<String, Integer>(), 12);
        final String expected1 = "{\"@PQON\":\"jsonTest.Json2WithMap\",\"en\":\"R\",\"map\":{\"A\":17},\"num\":12}\n";
        JsonComposer.setDefaultWriteCRs(false);

        j.getMap().put("A", 17);
        String j1 = JsonComposer.toJsonString(j);
        System.out.println("Bonaparte produces " + j1);
        Assertions.assertEquals(j1, expected1);
    }

    @Test
    public void runBonaMapOfObjects() throws Exception {
        final JsonWithMapOfObjects obj = new JsonWithMapOfObjects(ColorAlnum.RED, new HashMap<String, TestObj>(), 12);
        final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithMapOfObjects\",\"en\":\"R\",\"map\":{\"DE\":{\"primitiveInt\":49},\"FR\":{\"primitiveInt\":33}},\"num\":12}\n";
        JsonComposer.setDefaultWriteCRs(false);

        TestObj de = new TestObj();
        de.setPrimitiveInt(49);
        TestObj fr = new TestObj();
        fr.setPrimitiveInt(33);
        obj.getMap().put("DE", de);
        obj.getMap().put("FR", fr);
        String j1 = JsonComposer.toJsonString(obj);
        System.out.println("Bonaparte produces " + j1);
        Assertions.assertEquals(j1, expected1);
    }

    @Test
    public void runBonaLongMap() throws Exception {
        final JsonWithLongMap j = new JsonWithLongMap(new HashMap<Long, Integer>(), 12, new HashMap<Long, String>(), 24);
        final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithLongMap\",\"ciMap\":{\"_33\":17},\"num1\":12,\"caMap\":{\"_66\":\"Hello, world\"},\"num2\":24}\n";
        JsonComposer.setDefaultWriteCRs(false);

        j.getCiMap().put(33L, 17);
        j.getCaMap().put(66L, "Hello, world");
        String j1 = JsonComposer.toJsonString(j);
        System.out.println("Bonaparte produces " + j1);
        Assertions.assertEquals(j1, expected1);
    }

    @Test
    public void runBonaIntegerMap() throws Exception {
        final JsonWithIntegerMap j = new JsonWithIntegerMap(new HashMap<Integer, Integer>(), 12, new HashMap<Integer, String>(), 24);
        final String expected1 = "{\"@PQON\":\"jsonTest.JsonWithIntegerMap\",\"ciMap\":{\"_33\":17},\"num1\":12,\"caMap\":{\"_66\":\"Hello, world\"},\"num2\":24}\n";
        JsonComposer.setDefaultWriteCRs(false);

        j.getCiMap().put(33, 17);
        j.getCaMap().put(66, "Hello, world");
        String j1 = JsonComposer.toJsonString(j);
        System.out.println("Bonaparte produces " + j1);
        Assertions.assertEquals(j1, expected1);
    }
}
