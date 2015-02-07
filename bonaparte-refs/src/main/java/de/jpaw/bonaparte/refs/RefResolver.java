package de.jpaw.bonaparte.refs;

import de.jpaw.bonaparte.pojos.api.Ref;
import de.jpaw.bonaparte.pojos.api.TrackingBase;

/** API to noSQL backends (mini EntityManager) */
public interface RefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> {
    /**
     * Returns the key for the provided unique index. Null-safe, returns 0 for a null parameter. Throws an exception if the reference does not exist.
     */
    long getRef(REF refObject) throws PersistenceException;

    /**
     * Returns the DTO for a given primary key. Null-safe, returns null for ref <= 0. Throws an exception if the key does not exist.
     */
    DTO getDTO(long ref) throws PersistenceException;

    /**
     * Returns a frozen copy of the tracking columns (to avoid tampering with them) for a given primary key.
     */
    TRACKING getTracking(long ref) throws PersistenceException;

    /**
     * Returns the DTO for a given unique index. Null-safe, returns null for a null index value. Throws an exception if the key does not exist.
     */
    DTO getDTO(REF refObject) throws PersistenceException;

    /**
     * Removes the record referenced by the key. Does nothing if key = 0. Throws an exception if the key does not exist.
     */
    void remove(long key) throws PersistenceException;

    /**
     * Creates the DTO as provided in the database. Throws an exception if the key does already exist or the key is invalid (<= 0).
     */
    void create(DTO obj) throws PersistenceException;

    /**
     * Updates the DTO as provided in the database. Throws an exception if no record with the provided key exists or the key is invalid (<= 0).
     */
    void update(DTO obj) throws PersistenceException;

    /**
     * Clears the cache (should be called at end of a transaction).
     */
    void clear();

    /**
     * Flushes all modified but not yet written data to the database.
     */
    void flush();
}
