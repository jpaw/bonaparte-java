package de.jpaw.bonaparte.hazelcast;

import com.hazelcast.nio.serialization.Serializer;

public abstract class BonaparteSerializer implements Serializer {
    public static final int BONAPARTE_SERIALIZER_TYPE_ID = 58;

    @Override
    public int getTypeId() {
        return BONAPARTE_SERIALIZER_TYPE_ID;
    }

    @Override
    public void destroy() {
    }
}
