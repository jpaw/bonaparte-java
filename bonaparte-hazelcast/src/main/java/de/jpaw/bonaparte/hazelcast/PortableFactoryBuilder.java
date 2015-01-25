package de.jpaw.bonaparte.hazelcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.Portable;

import de.jpaw.bonaparte.core.BonaPortableClass;

/** Class to scan the class path and create factories as required. */
public class PortableFactoryBuilder extends AbstractScanner<BonapartePortable> {
    
    static class BonapartePortableFactory implements PortableFactory {
        private final ConcurrentHashMap<Integer, BonaPortableClass<BonapartePortable>> bclasses
            = new ConcurrentHashMap<Integer, BonaPortableClass<BonapartePortable>>(256);
        
        @Override
        public Portable create(int classId) {
            BonaPortableClass<BonapartePortable> bclass = bclasses.get(classId);
            return bclass == null ? null : bclass.newInstance();
        }
        
        private void addClass(BonaPortableClass<BonapartePortable> bclass) {
            BonaPortableClass<BonapartePortable> old = bclasses.putIfAbsent(bclass.getId(), bclass);
            if (old != null && old != bclass) {
                LOGGER.error("duplicate factoryId {} / classId {} for {} and {}", bclass.getFactoryId(), bclass.getId(), old.getPqon(), bclass.getPqon());
                throw new RuntimeException("Duplicate factory / class ID");
            }
        }
        
        private int size() {
            return bclasses.size();
        }
    }

    private final ConcurrentHashMap<Integer, BonapartePortableFactory> factoryMap
        = new ConcurrentHashMap<Integer, BonapartePortableFactory>(16);

    @Override
    protected void addBClass(BonaPortableClass<BonapartePortable> bclass) {
        Integer factoryId = bclass.getFactoryId();  // autobox only once
        BonapartePortableFactory myFactory = factoryMap.get(factoryId);
        if (myFactory == null) {
            // create a new factory, the factoryId occurs the first time now
            factoryMap.putIfAbsent(factoryId, new BonapartePortableFactory());
        }
        factoryMap.get(factoryId).addClass(bclass);
    }
    
    public void scanPackage(String packageName) {
        scanPackage(packageName, BonapartePortable.class);
    }
    
    public void registerFactories(SerializationConfig cfg) {
        for (Map.Entry<Integer, BonapartePortableFactory> e : factoryMap.entrySet()) {
            LOGGER.info("Registering PortableFactory for Id {} with {} classes", e.getKey(), e.getValue().size());
            cfg.addPortableFactory(e.getKey(), e.getValue());
        }
    }
}
