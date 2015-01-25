package de.jpaw.bonaparte.hazelcast.test;

import org.testng.annotations.Test;

import com.hazelcast.config.SerializationConfig;

import de.jpaw.bonaparte.hazelcast.IdentifiedDataSerializableFactoryBuilder;

public class ClassCollectorTest {

    @Test
    public void testIdentifiedDataSerializable() throws Exception {
        IdentifiedDataSerializableFactoryBuilder myBuilder = new IdentifiedDataSerializableFactoryBuilder();
        
        myBuilder.scanPackage("de.jpaw.bonaparte");
        myBuilder.registerFactories(new SerializationConfig());
    }
}
