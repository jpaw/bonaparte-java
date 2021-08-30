package de.jpaw.bonaparte.scanner;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.BonaPortableClass;
import de.jpaw.bonaparte.core.BonaPortableFactoryById;
import de.jpaw.xenums.init.ReflectionsPackageCache;

public class BClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(BClassScanner.class);

    static {
        scanAndRegisterBonaPortables("de.jpaw.bonaparte");  // meta and ui might be referenced somewhere...
    }

    /** Entry of no further packages need scanning. */
    public static void init() {
    }

    /** Entry for separate packages. */
    public static void scanAndRegisterBonaPortables(String packageName) {
        scanAndRegisterBonaPortables(ReflectionsPackageCache.get(packageName));
    }

    /** Scan a list of available reflections. */
    public static void scanAndRegisterBonaPortables(Reflections ... reflections) {
        for (int i = 0; i < reflections.length; ++i) {
            int ctr = 0;
            for (Class<? extends BonaPortable> cls : reflections[i].getSubTypesOf(BonaPortable.class)) {
                if (!cls.isInterface()) {
                    try {
                        BonaPortableClass<?> bclass = (BonaPortableClass<?>) cls.getMethod("class$BonaPortableClass").invoke(null);
                        if (BonaPortableFactoryById.registerClass(bclass))
                            ++ctr;
                    } catch (Exception e) {
                        LOGGER.warn("Cannot obtain BonaPortableClass for {}: {}", cls.getCanonicalName(), e.getMessage());
                    }
                }
            }
            LOGGER.info("Startup: Loaded {} BonaPortable classes", ctr);
        }
    }
}
