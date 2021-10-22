package de.jpaw.bonaparte.core;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.ClassDefinition;

public class BonaPortableFactoryById {
    private static final Logger LOG = LoggerFactory.getLogger(BonaPortableFactoryById.class);

    // Skip the next field and PathResolverTest may fail. Required to load the meta data classes in the correct order, required due to cyclic dependencies
    // of static fields and their initialization.
    public static Object UNUSED = ClassDefinition.class$MetaData();
    // it will definitely fail if for example FieldDefinition is loaded before ClassDefinition

    /** Stub to call to force initialization, in case no other initialization is required. */
    public static void init() {
    }

    // the big factory lookup map
    private static ConcurrentHashMap<Long,BonaPortableClass<?>> lookup = new ConcurrentHashMap<Long,BonaPortableClass<?>>(2048);

    /** Adds a new class to the registry. Returns true if the class has been accepted and was not known before. */
    public static boolean registerClass(BonaPortableClass<?> bclass) {
        if (bclass.getMetaData().getIsAbstract() || bclass.getFactoryId() == 0 || bclass.getId() == 0)
            return false;  // not relevant
        Long id = keyByIds(bclass.getFactoryId(), bclass.getId());
        BonaPortableClass<?> existing = lookup.putIfAbsent(id, bclass);
        if (existing != null) {
            // might at least issue a warning...
            if (existing != bclass) {
                String errorMsg = "Attempt to register 2 classes by the same ID: " + existing.getPqon() + " and " + bclass.getPqon();
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            return false;  // no new class
        }
        return true;
    }

    /** Composes the factoryId and the classId into a single Long, which is used as a key. */
    public static Long keyByIds(int factoryId, int classId) {
        return Long.valueOf((((long)factoryId) << 32) | classId);
    }

    /** Returns the factory Id portion of a composed long value. */
    public static int factoryIdByKey(long key) {
        return (int)(key >> 32);
    }

    /** Returns the class Id portion of a composed long value. */
    public static int classIdByKey(long key) {
        return (int)key;
    }

    /** Returns the BClass for the baked key. */
    public static BonaPortableClass<?> getByKey(Long key) {
        return lookup.get(key);
    }

    /** Returns the BClass for the ids. */
    public static BonaPortableClass<?> getByIds(int factoryId, int classId) {
        return getByKey(keyByIds(factoryId, classId));
    }
}
