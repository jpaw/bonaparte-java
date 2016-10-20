package de.jpaw.bonaparte.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongTools.class);

    public static long MAX_VALID_LONG = 1L << 53;
    public static long MIN_VALID_LONG = -MAX_VALID_LONG;

    public static boolean checkForLongOverflows = true;      // allows to disable these checks globally
    public static boolean exceptionOnLongOverflows = false;  // if true, will throw an IOException instead only logging a warning

    /** Validates that a long value can be retrieved exactly even if stored in a double temporarily (Javascript).
     * See http://stackoverflow.com/questions/1848700/biggest-integer-that-can-be-stored-in-a-double
     * @param l the value to check
     */
    public static void checkLongOverflow(long l) throws IOException {
        if (checkForLongOverflows) {
            if (l < MIN_VALID_LONG || l > MAX_VALID_LONG) {
                LOGGER.warn("Serializing long value of {} into JSON, Javascript may not be able to retrieve the exact value", l);
                if (exceptionOnLongOverflows)
                    throw new IOException("Serializing long value of " + l + " into JSON, Javascript may not be able to retrieve the exact value");
            }
        }
    }
}
