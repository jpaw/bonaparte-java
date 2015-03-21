package de.jpaw.bonaparte.core;

public interface BonaPortableRef extends BonaPortable {
    /**
     * Returns a technical reference of type long which can be used as a primary key in key/value stores.
     * This is possibly identical to a JPA ID, and possibly different (for entities which do not have a surrogate key).
     */
    long ret$RefP();

    /**
     * Returns a technical reference of type Long which can be used as a primary key in key/value stores.
     * This is possibly identical to a JPA ID, and possibly different (for entities which do not have a surrogate key).
     */
    Long ret$RefW();
}
