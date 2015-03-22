package de.jpaw.bonaparte.hazelcast.test;

import java.io.IOException;

import org.testng.annotations.Test;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;

import de.jpaw.bonaparte.pojos.hazeltest.DSTest;
import de.jpaw.bonaparte.pojos.hazeltest.IDSTest;
import de.jpaw.util.ByteUtil;

@Test
public class SerializationTest {

    static void doTest(Object obj, String msg, DataSerializableFactory f) throws IOException {
        System.out.println(msg);

//        Config cfg = new Config();
//        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
//        Map<Integer, DSTest> testMap = instance.getMap("dstest");
//        testMap.put(1, obj);

        // now obtain the raw data behind it
        SerializationServiceBuilder b = new DefaultSerializationServiceBuilder().setUseNativeByteOrder(true);
        final SerializationService ss = (f == null ? b : b.addDataSerializableFactory(12, f)).build();
        final Data data1              = ss.toData(obj);
        final Data data3              = ss.toData(obj);
        final ObjectDataOutput out2   = ss.createObjectDataOutput(1024);
        final ObjectDataOutput out3   = ss.createObjectDataOutput(1024);
        out2.writeObject(obj);
        out3.writeData(data3);
        
        byte [] bytes1 = data1.getData();           // shortest
        byte [] bytes2 = out2.toByteArray();        // 5 bytes more
        byte [] bytes3 = out3.toByteArray();        // 17 bytes more
        
        System.out.println("Size 1 is " + bytes1.length + ", size 2 is " + bytes2.length + ", size 3 is " + bytes3.length);
//        System.out.println("buff 1 is\n" + ByteUtil.dump(bytes1, 0));
//        System.out.println("buff 2 is\n" + ByteUtil.dump(bytes2, 0));
//        System.out.println("buff 3 is\n" + ByteUtil.dump(bytes3, 0));

        // parse again
        final ObjectDataInput in2 = ss.createObjectDataInput(bytes2);
        final ObjectDataInput in3 = ss.createObjectDataInput(bytes3);
        final Object rd2          = in2.readObject();
        final Data rd3            = in3.readData();
        
        assert(obj.equals(rd2));
        assert(data3.equals(rd3));
    }

    // simple String test
    public void testUTF() throws Exception {
        doTest("Hello, world!", "Test UTF", null);
    }

    // test data serializable
    public void testDataSerializable() throws Exception {

        DSTest tmp = new DSTest();
        tmp.hello = "Hello, world";
        tmp.num = 18;
        tmp.short1 = "0";
        tmp.short2 = "A";
        tmp.hello2 = "alallalallalong... alallalallalong... ding dong!";

        doTest(tmp, "Test DS", null);
    }

    // test identified data serializable
    public void testIdentifiedDataSerializable() throws Exception {

        IDSTest tmp = new IDSTest();
        tmp.hello = "Hello, world";
        tmp.num = 18;
        tmp.short1 = "0";
        tmp.short2 = "A";
        tmp.hello2 = "alallalallalong... alallalallalong... ding dong!";

        doTest(tmp, "Test IDS", new DataSerializableFactory() {
            @Override
            public IdentifiedDataSerializable create(int id) {
                return (id == 17) ? new IDSTest() : null;
            }
        });
    }
}
