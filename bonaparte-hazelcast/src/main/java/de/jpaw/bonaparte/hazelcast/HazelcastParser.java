package de.jpaw.bonaparte.hazelcast;

import java.io.DataInput;
import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.CompactParser;

public class HazelcastParser extends CompactParser {

    // entry called from generated objects:
    public static void deserialize(BonaPortable obj, ObjectDataInput _in) throws IOException {
        CompactParser.deserialize(obj, _in);
    }

    public HazelcastParser(ObjectDataInput in) {
        super(in);
        // TODO Auto-generated constructor stub
    }

}
