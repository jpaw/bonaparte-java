package de.jpaw.bonaparte.hazelcast;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.xenums.init.ReflectionsPackageCache;

public abstract class AbstractScanner<T extends BonaPortable> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractScanner.class);

    abstract void addBClass(BonaPortableClass<T> bclass);
    
    protected void scanPackage(String packageName, Class<T> interfaceType) {
        scanPackage(interfaceType, ReflectionsPackageCache.get(packageName));
    }
    
    public void scanPackage(Class<T> interfaceType, Reflections ... reflections) {
        for (int i = 0; i < reflections.length; ++i) {
            for (Class<? extends T> cls : reflections[i].getSubTypesOf(interfaceType)) {
                try {
                    BonaPortableClass<T> bclass = (BonaPortableClass<T>) cls.getMethod("class$BonaPortableClass").invoke(null);
                    if (bclass.getFactoryId() != 0 && bclass.getId() != 0)
                        addBClass(bclass);
                } catch (Exception e) {
                    LOGGER.warn("Cannot obtain BonaPortableClass for {}: {}", cls.getCanonicalName(), e.getMessage());
                }
            }
        }
    }
}
