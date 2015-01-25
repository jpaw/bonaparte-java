package de.jpaw.bonaparte.hazelcast;

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import de.jpaw.bonaparte.core.BonaPortable;

/** Common interface for classes implementing both DataSerializable and BonaPortable. */
public interface BonaparteIdentifiedDataSerializable extends BonaPortable, IdentifiedDataSerializable {
}
