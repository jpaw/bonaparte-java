package de.jpaw.bonaparte.core;

public interface BonaBuilder<T extends BonaPortable> {
    T build();
}
