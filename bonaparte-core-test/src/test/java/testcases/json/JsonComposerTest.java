package testcases.json;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.StringBuilderComposer;
import de.jpaw.bonaparte.pojos.jsonTest.JsonFieldTest;
import de.jpaw.bonaparte.util.ToStringHelper;
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

}
