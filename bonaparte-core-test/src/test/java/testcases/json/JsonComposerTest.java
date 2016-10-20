package testcases.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.jsonTest.ColorAlnum;
import de.jpaw.bonaparte.pojos.jsonTest.ColorNum;
import de.jpaw.bonaparte.pojos.jsonTest.JsonEnumAndList;
import de.jpaw.bonaparte.pojos.jsonTest.JsonFieldTest;
import de.jpaw.bonaparte.pojos.jsonTest.XColor;
import de.jpaw.bonaparte.util.ToStringHelper;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ByteUtil;

public class JsonComposerTest {
    static private List<Object> MY_VAR_LIST = new ArrayList<Object>(3);
    static {
        MY_VAR_LIST.add(42);
        MY_VAR_LIST.add("Hello");
        MY_VAR_LIST.add(true);
    }


    @Test
    public void runBonaTest1() throws Exception {
        JsonFieldTest t = new JsonFieldTest();

        t.setText("mytext");
        t.setVarField(new LocalDate(2015,10,31));

        List<Object> l = new ArrayList<Object>(20);
        t.setVarList(l);
        l.add(42);
        l.add(3.14);
        l.add('x');
        l.add("Hello, world");

        t.setVarList2(MY_VAR_LIST);

        StringBuilder sb = new StringBuilder();
        StringBuilderComposer sbc = new StringBuilderComposer(sb);
        sbc.writeRecord(t);
        System.out.println("Result is " + sb.toString());

        CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeRecord(t);
        byte [] b = cbac.getBytes();
        System.out.println("Result is " + b.length);
        System.out.println(ByteUtil.dump(b, 1000));
    }

    @Test
    public void runBonaTest2() throws Exception {
        JsonFieldTest t = new JsonFieldTest();

        t.setText("mytext");
        t.setVarField(JsonFieldTest.class$MetaData());
        List<Object> l = new ArrayList<Object>(20);
        t.setVarList(l);
        l.add(42);
        l.add(3.14);
        l.add('x');
        l.add("Hello, world");
        t.setVarList2(MY_VAR_LIST);

        CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeRecord(t);
        byte [] b = cbac.getBytes();

        // deserialize again
        CompactByteArrayParser cbap = new CompactByteArrayParser(b, 0, -1);
        BonaPortable t2 = cbap.readRecord();
        System.out.println(ToStringHelper.toStringML(t2));

        System.out.println(JsonComposer.toJsonString(t2));
    }

    private JsonEnumAndList testObject() {
        JsonEnumAndList t = new JsonEnumAndList();

        t.setCn(ColorNum.GREEN);
        t.setCa(ColorAlnum.GREEN);
        t.setCx(XColor.myFactory.getByName("RED"));
        List<Object> l = new ArrayList<Object>(20);
        t.setAny(l);
        l.add(42);
        l.add(3.14);
        l.add(ColorAlnum.GREEN);
        l.add('x');
        l.add("Hello, world");
        return t;
    }

    @Test
    public void runJsonEnumAndListCompactTest() throws Exception {
        JsonEnumAndList t = testObject();

        CompactByteArrayComposer cbac = new CompactByteArrayComposer();
        cbac.writeRecord(t);
        byte [] b = cbac.getBytes();

        // deserialize again
        CompactByteArrayParser cbap = new CompactByteArrayParser(b, 0, -1);
        BonaPortable t2 = cbap.readRecord();
        System.out.println(ToStringHelper.toStringML(t2));

        System.out.println(JsonComposer.toJsonString(t2));
    }

    @Test
    public void runJsonEnumAndListTest() throws Exception {
        JsonEnumAndList t = testObject();

        StringBuilder buff = new StringBuilder(200);
        JsonComposer cbac = new JsonComposer(buff);
        cbac.writeRecord(t);

        System.out.println(buff);       // visually verify: array, enum names

        Object obj = new JsonParser(buff, false).parseElement();
        System.out.println(ToStringHelper.toStringML(obj));       // visually verify: array, enum names

        Assert.assertTrue(obj instanceof Map);
        Map<?,?> objM = (Map<?,?>)obj;
        Object expL = objM.get("any");
        Assert.assertNotNull(expL);
        Assert.assertTrue(expL instanceof List);
        List<?> objL = (List<?>)expL;
        Assert.assertEquals(objL.size(), 5);
        Assert.assertEquals(objM.get("cn"), Integer.valueOf(1));
        Assert.assertEquals(objM.get("ca"), "G");
        Assert.assertEquals(objM.get("cx"), "R");
    }

    @Test
    public void runJsonEnumAndListAsMapTest() throws Exception {
        JsonEnumAndList t = testObject();

        MapComposer cbac = new MapComposer();
        cbac.writeRecord(t);
        Map<String, Object> map = cbac.getStorage();

        System.out.println(ToStringHelper.toStringML(map));       // visually verify: array, enum names

        MapParser mp = new MapParser(map, false);
        Object obj = mp.readElement(StaticMeta.OUTER_BONAPORTABLE_FOR_ELEMENT);
        System.out.println(ToStringHelper.toStringML(obj));       // visually verify: array, enum names
    }

    @Test
    public void runJsonNestedEnumAndListTest() throws Exception {
        JsonEnumAndList t1 = testObject();
        JsonEnumAndList t2 = testObject();

        t1.setAny(t2);
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(t1);

        String txt = new String(bac.getBytes(), "UTF-8");
        System.out.println(txt);       // visually verify: array, enum names

        // variant 2: MapComposer.marshal
        Map<?,?> objM = MapComposer.marshal(t1);
        System.out.println(ToStringHelper.toStringML(objM));       // visually verify: array, enum names => OK
    }

    @Test
    public void runJsonNestedEnumAndList2Test() throws Exception {
        JsonEnumAndList t1 = testObject();
        JsonEnumAndList t2 = testObject();

        t1.setAny(MapComposer.marshal(t2));
        ByteArrayComposer bac = new ByteArrayComposer();
        bac.writeRecord(t1);

        String txt = new String(bac.getBytes(), "UTF-8");
        System.out.println(txt);       // visually verify: array, enum names

        Map<String,Object> bonInMap = new HashMap<String, Object>(4);
        bonInMap.put("content", t1);   // map includes bon includes map
        JsonEnumAndList t3 = testObject();
        t3.setAny(bonInMap);
        Map<?,?> objM = MapComposer.marshal(t3);
        System.out.println(ToStringHelper.toStringML(objM));       // visually verify: array, enum names => OK

        bac.reset();
        bac.writeRecord(t3);
        String txt2 = new String(bac.getBytes(), "UTF-8");
        System.out.println(txt2);       // visually verify: array, enum names
    }

    @Test
    public void runJsonNestedEnumAndList3Test() throws Exception {
        JsonEnumAndList t1 = testObject();
        JsonEnumAndList t2 = testObject();

        t1.setAny(MapComposer.marshal(t2));
        CompactByteArrayComposer bac = new CompactByteArrayComposer();
        bac.writeRecord(t1);

        // expand again
        CompactByteArrayParser cbap = new CompactByteArrayParser(bac.getBuffer(), 0, bac.getLength());
        BonaPortable obj = cbap.readRecord();
        System.out.println(ToStringHelper.toStringML(obj));       // visually verify: array, enum names
    }
}
