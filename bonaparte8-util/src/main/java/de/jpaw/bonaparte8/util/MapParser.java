package de.jpaw.bonaparte8.util;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringProviderParser;

public class MapParser {
    /** Populates a BonaPortable from a key/value map, with stringified contents. */
    public static void unmarshal(BonaPortable obj, Map<String,String> data) throws MessageParserException {
        obj.deserialize(new StringProviderParser(data::get, obj.ret$PQON()));
    }
}
