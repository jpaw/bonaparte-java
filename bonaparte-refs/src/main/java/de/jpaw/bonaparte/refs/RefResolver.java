package de.jpaw.bonaparte.refs;

import de.jpaw.bonaparte.pojos.refs.RefLong;

// API to the in-memory-DB backend (mini EntityManager)
public interface RefResolver<REF extends RefLong, DTO extends REF> {
//    int getRtti();                                    // returns the RTTI of the class
    long getRef(REF refObject);                         // return the primary key for an object, or 0 if the reference points to an invalid object
    DTO getDTO(REF refObject);                          // return the full object, or null if the reference was invalid.
    DTO getDTO(long ref);                               // returns the DTO for a given key, or null if the reference is <= 0. Throws an exception if the reference is invalid
    void remove(long key) throws PersistenceException;  // removes the record
    long create(DTO obj) throws PersistenceException;   // if the object exists an Exception is thrown. The new key is returned. 
    void update(DTO obj) throws PersistenceException;   // if the object does not exist an Exception is thrown. DTO contains the key.
    // DTO newInstance();                               // creates a new DTO object, also assigns a primary key already  (needs the tenant)
    void clear();                                       // clear the cache (at end of transaction)
    void flush();                                       // writes back dirty records (uncommitted) to the 2nd DB 
}
