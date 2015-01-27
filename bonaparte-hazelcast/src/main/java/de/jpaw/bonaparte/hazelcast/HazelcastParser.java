package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactParser;

public class HazelcastParser {

    // entry called from generated objects:
    public static void deserialize(BonaPortable obj, ObjectDataInput _in) throws IOException {
        CompactParser.deserialize(obj, _in);
    }

    // currently, no instances are supported
    private HazelcastParser() {
    }
}
