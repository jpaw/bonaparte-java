package de.jpaw.bonaparte.refsp;

import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.bonaparte.pojos.apip.Ref;
import de.jpaw.util.ApplicationException;

/** API to noSQL backends (mini EntityManager) */
public interface RefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> {
    /**
     * Returns the key for the provided unique index. Null-safe, returns 0 for a null parameter. Throws an exception if the reference does not exist.
     */
    long getRef(REF refObject) throws ApplicationException;

    /**
     * Returns the DTO for a given primary key. Null-safe, returns null for ref <= 0. Throws an exception if the key does not exist.
     */
    DTO getDTO(long ref) throws ApplicationException;

    /**
     * Returns a frozen copy of the tracking columns (to avoid tampering with them) for a given primary key.
     */
    TRACKING getTracking(long ref) throws ApplicationException;

    /**
     * Returns the DTO for a given unique index. Null-safe, returns null for a null index value. Throws an exception if the key does not exist.
     */
    DTO getDTO(REF refObject) throws ApplicationException;

    /**
     * Removes the record referenced by the key. Does nothing if key = 0. Throws an exception if the key does not exist.
     */
    void remove(long key) throws ApplicationException;

    /**
     * Creates the DTO as provided in the database. Throws an exception if the key does already exist or the key is invalid (<= 0).
     */
    void create(DTO obj) throws ApplicationException;

    /**
     * Updates the DTO as provided in the database. Throws an exception if no record with the provided key exists or the key is invalid (<= 0).
     */
    void update(DTO obj) throws ApplicationException;

    /**
     * Clears the cache (should be called at end of a transaction).
     */
    void clear();

    /**
     * Flushes all modified but not yet written data to the database.
     */
    void flush();
}
