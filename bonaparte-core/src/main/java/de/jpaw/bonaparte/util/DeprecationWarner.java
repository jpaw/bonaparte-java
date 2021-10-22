package de.jpaw.bonaparte.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeprecationWarner {

    public static class DefaultWarner implements IDeprecationWarner {
        static final private Logger LOGGER = LoggerFactory.getLogger(DefaultWarner.class);
        final String action;

        public DefaultWarner(String forWhichAction) {
            action = forWhichAction;
        }

        @Override
        public void warn(Object obj, String fieldName) {
            LOGGER.warn("Using {} for deprecated field {}.{}", action, obj.getClass().getCanonicalName(), fieldName);
        }
    }

    /** Assign your preferred implementation, for example to obtain stack traces etc. */
    public static IDeprecationWarner setWarner = new DefaultWarner("setter");
    public static IDeprecationWarner getWarner = null;

    public static void warnSet(Object obj, String fieldName) {
        if (setWarner != null)
            setWarner.warn(obj, fieldName);
    }

    public static void warnGet(Object obj, String fieldName) {
        if (getWarner != null)
            getWarner.warn(obj, fieldName);
    }
}
