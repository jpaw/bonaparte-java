package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.serialization.PortableReader;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactParser;

public class HazelcastPortableParser {
    // entry called from generated objects:
    public static void deserialize(BonaPortable obj, PortableReader _in) throws IOException {
        // ignore any specific fields, because the full data is in the appendix
        
        ObjectDataInput in = _in.getRawDataInput();     // extends java.io.DataInput
        CompactParser.deserialize(obj, in);
    }

    // no instances currently
    private HazelcastPortableParser() {
    }
}
