package testcases.json;

import java.util.Map;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
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
        LocalDateTime ld0 = new LocalDateTime(2016, 12, 31, 17, 59, 59);
        LocalDateTime ld3 = new LocalDateTime(2016, 12, 31, 17, 59, 59, 333);
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
        LocalTime l0 = new LocalTime(17, 59, 59);
        LocalTime l3 = new LocalTime(17, 59, 59, 333);
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
