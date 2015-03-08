package testcases.xenum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.testng.annotations.Test;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.testXenum.BW;
import de.jpaw.bonaparte.pojos.testXenum.Color;
import de.jpaw.bonaparte.pojos.testXenum.SimpleSampleUsingInheritedXEnum;
import de.jpaw.bonaparte.pojos.testXenum.SimpleSampleUsingXEnum;

public class TestXenumSerialization {

    static public byte [] serialize(Object x) throws IOException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream(1000);
        ObjectOutputStream o = new ObjectOutputStream(fos);
        o.writeObject(x);
        o.close();
        byte[] result = fos.toByteArray();
        System.out.println("Length of buffer is " + result.length);
        return result;
    }

    static Object deserialize(byte [] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream fis = new ByteArrayInputStream(data);
        ObjectInputStream i = new ObjectInputStream(fis);
        Object r = i.readObject();
        i.close();
        return r;
    }

    public void testEncodeDecode(BonaPortable obj) throws Exception {
        byte [] res1 = serialize(obj);
        Object r1 = deserialize(res1);
        System.out.println("Got object " + r1.getClass().getName());
        assert(r1 != null);
        assert(r1 instanceof BonaPortable);
//        System.out.println("Org object is " + ToStringHelper.toStringML(obj));
//        System.out.println("New object is " + ToStringHelper.toStringML((BonaPortable)r1));
        assert obj.equals(r1);
        assert(obj.getClass().equals(r1.getClass()));
    }

    @Test
    public void testEncodeDecodeBaseLow() throws Exception {
        SimpleSampleUsingXEnum obj = new SimpleSampleUsingXEnum();
        obj.setMyColor(Color.GREEN);
        testEncodeDecode(obj);
    }
    @Test
    public void testEncodeDecodeInheritedLow() throws Exception {
        SimpleSampleUsingInheritedXEnum obj = new SimpleSampleUsingInheritedXEnum();
        obj.setMyColor(Color.GREEN);
        testEncodeDecode(obj);
    }
    @Test
    public void testEncodeDecodeInheritedHigh() throws Exception {
        SimpleSampleUsingInheritedXEnum obj = new SimpleSampleUsingInheritedXEnum();
        obj.setMyColor(BW.WHITE);
        testEncodeDecode(obj);
    }
    @Test
    public void testEncodeDecodeBaseHigh() throws Exception {
        @SuppressWarnings("unused")
        int i = SimpleSampleUsingInheritedXEnum.class$rtti();  // initialize high-values. Won't work reliably without this statement
        SimpleSampleUsingXEnum obj = new SimpleSampleUsingXEnum();
        obj.setMyColor(BW.WHITE);
        testEncodeDecode(obj);
    }
}
