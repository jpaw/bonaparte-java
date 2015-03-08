package de.jpaw.bonaparte.hazelcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import de.jpaw.bonaparte.core.BonaPortableClass;

/** Class to scan the class path and create factories as required. */
public class IdentifiedDataSerializableFactoryBuilder extends AbstractScanner<BonaparteIdentifiedDataSerializable> {

    static class BonaparteIdentifiedDataSerializableFactory implements DataSerializableFactory {
        private final ConcurrentHashMap<Integer, BonaPortableClass<BonaparteIdentifiedDataSerializable>> bclasses
            = new ConcurrentHashMap<Integer, BonaPortableClass<BonaparteIdentifiedDataSerializable>>(256);

        @Override
        public IdentifiedDataSerializable create(int classId) {
            BonaPortableClass<BonaparteIdentifiedDataSerializable> bclass = bclasses.get(classId);
            return bclass == null ? null : bclass.newInstance();
        }

        private void addClass(BonaPortableClass<BonaparteIdentifiedDataSerializable> bclass) {
            BonaPortableClass<BonaparteIdentifiedDataSerializable> old = bclasses.putIfAbsent(bclass.getId(), bclass);
            if (old != null && old != bclass) {
                LOGGER.error("duplicate factoryId {} / classId {} for {} and {}", bclass.getFactoryId(), bclass.getId(), old.getPqon(), bclass.getPqon());
                throw new RuntimeException("Duplicate factory / class ID");
            }
        }

        private int size() {
            return bclasses.size();
        }
    }

    private final ConcurrentHashMap<Integer, BonaparteIdentifiedDataSerializableFactory> factoryMap
        = new ConcurrentHashMap<Integer, BonaparteIdentifiedDataSerializableFactory>(16);

    @Override
    protected void addBClass(BonaPortableClass<BonaparteIdentifiedDataSerializable> bclass) {
        Integer factoryId = bclass.getFactoryId();  // autobox only once
        BonaparteIdentifiedDataSerializableFactory myFactory = factoryMap.get(factoryId);
        if (myFactory == null) {
            // create a new factory, the factoryId occurs the first time now
            factoryMap.putIfAbsent(factoryId, new BonaparteIdentifiedDataSerializableFactory());
        }
        factoryMap.get(factoryId).addClass(bclass);
    }

    public void scanPackage(String packageName) {
        scanPackage(packageName, BonaparteIdentifiedDataSerializable.class);
    }

    public void scanPackage(Reflections ... reflections) {
        scanPackage(BonaparteIdentifiedDataSerializable.class, reflections);
    }

    public void registerFactories(SerializationConfig cfg) {
        for (Map.Entry<Integer, BonaparteIdentifiedDataSerializableFactory> e : factoryMap.entrySet()) {
            LOGGER.info("Registering DataSerializableFactory for Id {} with {} classes", e.getKey(), e.getValue().size());
            cfg.addDataSerializableFactory(e.getKey(), e.getValue());
        }
    }
}
