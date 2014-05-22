package de.jpaw.bonaparte.hazelcast;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataOutput;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactComposer;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.StaticMeta;

public class HazelcastComposer extends CompactComposer {
    
    // entry called from generated objects:
    public static void serialize(BonaPortable obj, ObjectDataOutput _out, boolean recommendIdentifiable) throws IOException {
    	MessageComposer<IOException> _w = new HazelcastComposer(_out, recommendIdentifiable);
    	obj.serializeSub(_w);
    	_w.terminateObject(StaticMeta.OUTER_BONAPORTABLE, obj);
    }

    protected final ObjectDataOutput out;
    
	public HazelcastComposer(ObjectDataOutput out, boolean recommendIdentifiable) {
		super(out, recommendIdentifiable);
		this.out = out;
	}

    /** returns the result as byte array */
    public byte[] getBytes() {
        return out.toByteArray();
    }

}
