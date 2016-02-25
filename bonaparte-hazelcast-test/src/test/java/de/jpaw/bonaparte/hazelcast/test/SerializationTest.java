package de.jpaw.bonaparte.hazelcast.test;

import java.io.IOException;

import org.testng.annotations.Test;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import com.hazelcast.nio.serialization.Serializer;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableFactoryById;
import de.jpaw.bonaparte.hazelcast.BonaparteByteArraySerializer;
import de.jpaw.bonaparte.hazelcast.BonaparteStreamSerializer;
import de.jpaw.bonaparte.pojos.hazeltest.DSTest;
import de.jpaw.bonaparte.pojos.hazeltest.IDSTest;
import de.jpaw.bonaparte.pojos.hazeltest.PojoTest;

@Test
public class SerializationTest {

    private static void doTest(Object obj, String msg, DataSerializableFactory f, Serializer s) throws IOException {
        System.out.println(msg);

//        Config cfg = new Config();
//        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
//        Map<Integer, DSTest> testMap = instance.getMap("dstest");
//        testMap.put(1, obj);

        // now obtain the raw data behind it
        SerializationServiceBuilder b = new DefaultSerializationServiceBuilder().setUseNativeByteOrder(true);
        if (s != null) {
            b.setConfig(new SerializationConfig().addSerializerConfig(new SerializerConfig()
                                                    .setTypeClass(BonaPortable.class)
                                                    .setImplementation(s)));
        }
        final SerializationService ss = (f == null ? b : b.addDataSerializableFactory(12, f)).build();
        //final Data data1              = ss.toData(obj);
        final Data data3              = ss.toData(obj);
        final ObjectDataOutput out2   = ss.createObjectDataOutput(1024);
        final ObjectDataOutput out3   = ss.createObjectDataOutput(1024);
        out2.writeObject(obj);
        out3.writeData(data3);

        //byte [] bytes1 = data1.getData();           // shortest => existed with 3.4.x, no longer in 3.5.x
        byte [] bytes2 = out2.toByteArray();        // 5 bytes more     => this one is the recommended one to use
        byte [] bytes3 = out3.toByteArray();        // 17 bytes more

        System.out.println("Size 1 is " + 0 /* bytes1.length */ + ", size 2 is " + bytes2.length + ", size 3 is " + bytes3.length);
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
        doTest("Hello, world!", "Test UTF", null, null);
    }

    // test data serializable
    public void testDataSerializable() throws Exception {

        DSTest tmp = new DSTest();
        tmp.hello = "Hello, world";
        tmp.num = 18;
        tmp.short1 = "0";
        tmp.short2 = "A";
        tmp.hello2 = "alallalallalong... alallalallalong... ding dong!";

        doTest(tmp, "Test DS", null, null);
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
        }, null);
    }

    private static void runPojoSerializer(String what, Serializer s) throws Exception {
        BonaPortableFactoryById.registerClass(PojoTest.BClass.INSTANCE);

        PojoTest tmp = new PojoTest();
        tmp.hello = "Hello, world";
        tmp.num = 18;
        tmp.short1 = "0";
        tmp.short2 = "A";
        tmp.hello2 = "alallalallalong... alallalallalong... ding dong!";

        doTest(tmp, what, null, s);
    }

    public void testByteArraySerializerCid() throws Exception {
        runPojoSerializer("Test Pojo byte array CID", new BonaparteByteArraySerializer(true));
    }
    public void testByteArraySerializerPqon() throws Exception {
        runPojoSerializer("Test Pojo byte array PQON", new BonaparteByteArraySerializer(false));
    }
    public void testStreamSerializerCid() throws Exception {
        runPojoSerializer("Test Pojo stream CID", new BonaparteStreamSerializer(true));
    }
    public void testStreamSerializerPqon() throws Exception {
        runPojoSerializer("Test Pojo stream PQON", new BonaparteStreamSerializer(false));
    }
}
