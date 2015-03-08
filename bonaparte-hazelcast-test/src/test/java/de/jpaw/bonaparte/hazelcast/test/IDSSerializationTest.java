package de.jpaw.bonaparte.hazelcast.test;

import java.io.IOException;

import org.testng.annotations.Test;

import com.hazelcast.config.Config;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;

import de.jpaw.bonaparte.pojos.hazeltest.IDSTest;
import de.jpaw.util.ByteUtil;

@Test
public class IDSSerializationTest {

    private void dotstIDs(IDSTest obj) throws IOException {
        System.out.println("SUB IDS");

        Config cfg = new Config();

        // Java 1.8
        // cfg.getSerializationConfig().addDataSerializableFactory (12, (int id) -> (id == 17) ? new IDSTest() : null);
        // Java 1.7
        cfg.getSerializationConfig().addDataSerializableFactory (12, new DataSerializableFactory() {
            @Override
            public IdentifiedDataSerializable create(int id) {
                return (id == 17) ? new IDSTest() : null;
            }
        });


        // for the serialization test, no hazelcast instance is required
//        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
//        Map<Integer, IDSTest> testMap = instance.getMap("dstest");
//        testMap.put(1, obj);

        // now obtain the raw data behind it
        SerializationService ss = new SerializationServiceBuilder().setUseNativeByteOrder(true).build();
        final Data data1 = ss.toData(obj);

        ObjectDataOutput out = ss.createObjectDataOutput(1024);
        data1.writeData(out);
        byte[] bytes1 = out.toByteArray();

        ObjectDataOutput out2 = ss.createObjectDataOutput(1024);
        out2.writeObject(obj);
        byte[] bytes2 = out2.toByteArray();
        System.out.println("Size 1 is " + bytes1.length + ", size 2 is " + bytes2.length);
        System.out.println("buff 2 is\n" + ByteUtil.dump(bytes2, 0));
    }
    public void testIdentifiedDataSerializable() throws Exception {
        System.out.println("Test IDS");

        IDSTest tmp = new IDSTest();
        tmp.hello = "Hello, world";
        tmp.num = 18;
        tmp.short1 = "0";
        tmp.short2 = "A";
        tmp.hello2 = "alallalallalong... alallalallalong... ding dong!";

        dotstIDs(tmp);
    }

}
