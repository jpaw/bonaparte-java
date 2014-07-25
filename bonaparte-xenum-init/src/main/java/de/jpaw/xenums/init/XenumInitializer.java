package de.jpaw.xenums.init;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.enums.AbstractXEnumBase;

public class XenumInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(XenumInitializer.class);
    
    public static void initializeXenums(String packageName) {
        int ctr = 0;
        for (Class<? extends AbstractXEnumBase> cls : new Reflections(packageName).getSubTypesOf(AbstractXEnumBase.class)) {
            try {
                cls.getMethod("xenum$MetaData").invoke(null);
                ++ctr;
            } catch (Exception e) {
                LOGGER.warn("Cannot initialize xenum {}: {}", cls.getCanonicalName(), e.getMessage());
            }
        }
        LOGGER.info("Startup: Loaded {} xenum classes", ctr);
    }
}
