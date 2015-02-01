package de.jpaw.bonaparte.refs;

import de.jpaw.bonaparte.pojos.api.DataWithTracking;
import de.jpaw.bonaparte.pojos.api.Ref;
import de.jpaw.bonaparte.pojos.api.TrackingBase;

// API to the in-memory-DB backend (mini EntityManager)
public interface RefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> {
    // int getRtti(); // returns the RTTI of the class
    // DTO newInstance(); // creates a new DTO object, also assigns a primary key already (needs the tenant)

    /**
     * Returns the key for the provided unique index. Null-safe, returns 0 for a null parameter. Throws an exception if the reference does not exist.
     */
    long getRef(REF refObject) throws PersistenceException;

    /**
     * Returns the DTO and tracking for a given primary key. Null-safe, returns null for ref <= 0. Throws an exception if the key does not exist.
     */
    DataWithTracking<DTO, TRACKING> getDTO(long ref) throws PersistenceException;

    /**
     * Returns the DTO and tracking for a given unique index. Null-safe, returns null for a null index value. Throws an exception if the key does not exist.
     */
    DataWithTracking<DTO, TRACKING> getDTO(REF refObject) throws PersistenceException;

    /**
     * Removes the record referenced by the key. Does nothing if key = 0. Throws an exception if the key does not exist.
     */
    void remove(long key) throws PersistenceException;

    // /** Removes all record for which the provided key matches. Unique as well as non unique indexes are allowed here.
    // * Throws an exception in case of internal errors of if the class is not known.
    // * Returns the number of records actually deleted.
    // */
    // int removeAll(REF refObject) throws PersistenceException;

    /**
     * Creates the DTO as provided in the database. Returns the DTO with updated tracking data. Throws an exception if the key does already exist or the key is
     * invalid (<= 0).
     */
    DataWithTracking<DTO, TRACKING> create(DTO obj) throws PersistenceException;

    /**
     * Updates the DTO as provided in the database. Returns the DTO with updated tracking data. Throws an exception if no record with the provided key exists or
     * the key is invalid (<= 0).
     */
    DataWithTracking<DTO, TRACKING> update(DTO obj) throws PersistenceException;

    /**
     * Clears the cache (should be called at end of a transaction).
     */
    void clear();

    /**
     * Flushes all modified but not yet written data to the database.
     */
    void flush();
}
