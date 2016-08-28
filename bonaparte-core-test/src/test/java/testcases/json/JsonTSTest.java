package testcases.json;

import java.util.Map;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.jsonTest.TestTS;
import de.jpaw.bonaparte.pojos.jsonTest.TestT;
import de.jpaw.json.JsonParser;

public class JsonTSTest {

    @Test
    public void testTS() throws Exception {
        LocalDateTime ld0 = LocalDateTime.of(2016, 12, 31, 17, 59, 59);
        LocalDateTime ld3 = LocalDateTime.of(2016, 12, 31, 17, 59, 59, 333000000);
        TestTS ts = new TestTS();
        ts.setTs0(ld0);
        ts.setTs3(ld3);
        
        String result = JsonComposer.toJsonString(ts);
        System.out.println("Result is " + result);
        
        Map<String, Object> map = (new JsonParser(result, false)).parseObject();
        TestTS out = new TestTS();
        MapParser.populateFrom(out, map);
        
        Assert.assertEquals(out,  ts);
    }

    @Test
    public void testT() throws Exception {
        LocalTime l0 = LocalTime.of(17, 59, 59);
        LocalTime l3 = LocalTime.of(17, 59, 59, 333000000);
        TestT ts = new TestT();
        ts.setT0(l0);
        ts.setT3(l3);
        
        String result = JsonComposer.toJsonString(ts);
        System.out.println("Result is " + result);
        
        Map<String, Object> map = (new JsonParser(result, false)).parseObject();
        TestT out = new TestT();
        MapParser.populateFrom(out, map);
        
        Assert.assertEquals(out,  ts);
    }
}
