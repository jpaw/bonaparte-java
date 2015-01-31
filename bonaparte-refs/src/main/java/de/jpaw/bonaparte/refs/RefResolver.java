package de.jpaw.bonaparte.refs;

import de.jpaw.bonaparte.pojos.refs.RefLong;

public interface RefResolver<REF extends RefLong, DTO extends REF> {
    int getRtti();                  // returns the RTTI of the class
    long getRef(REF refObject);     // return the primary key for an object, or 0 if the reference points to an invalid object
    DTO getDTO(REF refObject);      // return the full object, or null if the reference was invalid.
    DTO getDTO(long ref);           // returns the DTO for a given key, or null if the reference is <= 0. Throws an exception if the reference is invalid
    // DTO newInstance();              // creates a new DTO object, also assigns a primary key already  (needs the tenant)
}
