package de.jpaw.bonaparte.hazelcast;

import com.hazelcast.nio.serialization.Portable;

import de.jpaw.bonaparte.core.BonaPortable;

/** Common interface for classes implementing both Portable and BonaPortable. */
public interface BonapartePortable extends BonaPortable, Portable {
}
