package de.jpaw.bonaparte.refsw;

import de.jpaw.bonaparte.pojos.apiw.Ref;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.bonaparte.refs.PersistenceException;

/** API to noSQL backends (mini EntityManager) */
public interface RefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> {
    /**
     * Returns the key for the provided unique index. Null-safe, returns null for a null parameter. Throws an exception if the reference does not exist.
     */
    Long getRef(REF refObject) throws PersistenceException;

    /**
     * Returns the DTO for a given primary key. Null-safe, returns null for a null ref. Throws an exception if the key does not exist.
     */
    DTO getDTO(Long ref) throws PersistenceException;

    /**
     * Returns a frozen copy of the tracking columns (to avoid tampering with them) for a given primary key.
     */
    TRACKING getTracking(Long ref) throws PersistenceException;

    /**
     * Returns the DTO for a given unique index. Null-safe, returns null for a null index value. Throws an exception if the key does not exist.
     */
    DTO getDTO(REF refObject) throws PersistenceException;

    /**
     * Removes the record referenced by the key. Does nothing if key is null. Throws an exception if the key does not exist.
     */
    void remove(Long key) throws PersistenceException;

    /**
     * Creates the DTO as provided in the database. Throws an exception if the key does already exist or the key is null.
     */
    void create(DTO obj) throws PersistenceException;

    /**
     * Updates the DTO as provided in the database. Throws an exception if no record with the provided key exists or the key is null.
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
