package de.jpaw.bonaparte.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.json.JsonException;
import de.jpaw.util.ByteUtil;

/** Class which holds a static method which is called if deserializing some object results in an Exception.
 *
 */
public class DeserializeExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(DeserializeExceptionHandler.class);
    public static final int DUMP_BYTES_BEFORE = 48;
    public static final int DUMP_BYTES_AFTER = 32;

    public static void exceptionHandler(String fieldname, byte [] offendingData, MessageParserException e, Class<?> clazz, Object entityKey) {
        // first, dump out a bit of information so we can analyze better...
        int badPosition = e.getCharacterIndex();
        LOG.error("Serialized data is corrupt for entity {} with key {} at byte position {}: {}",
                clazz.getSimpleName(),
                entityKey != null ? entityKey : "N/A",
                badPosition,
                e.getMessage());
        LOG.error(ByteUtil.dump(offendingData, badPosition - DUMP_BYTES_BEFORE, badPosition + DUMP_BYTES_AFTER));
        throw new IllegalArgumentException("Cannot parse serialized data for " + fieldname + ": " + e.getMessage());
    }

    public static void exceptionHandler(String fieldname, String offendingData, JsonException e, Class<?> clazz, Object entityKey) {
        // first, dump out a bit of information so we can analyze better...
        LOG.error("Serialized data is corrupt for entity {} with key {}: {}",
                clazz.getSimpleName(),
                entityKey != null ? entityKey : "N/A",
                e.getMessage());
        throw new IllegalArgumentException("Cannot parse serialized data for " + fieldname + ": " + e.getMessage());
    }
}
