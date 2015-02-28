package de.jpaw.bonaparte.refsw;

import de.jpaw.bonaparte.pojos.api.AbstractRef;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.bonaparte.refs.BaseRefResolver;
import de.jpaw.util.ApplicationException;

/** API to noSQL backends (mini EntityManager) */
public interface RefResolver<REF extends AbstractRef, DTO extends REF, TRACKING extends TrackingBase> extends BaseRefResolver<REF, DTO, TRACKING> {
    /**
     * Returns the key for the provided unique index. Null-safe, returns null for a null parameter. Throws an exception if the reference does not exist.
     */
    Long getRef(REF refObject) throws ApplicationException;

    /**
     * Returns the DTO for a given primary key. Null-safe, returns null for a null ref. Throws an exception if the key does not exist.
     */
    DTO getDTO(Long ref) throws ApplicationException;

    /**
     * Returns a frozen copy of the tracking columns (to avoid tampering with them) for a given primary key.
     */
    TRACKING getTracking(Long ref) throws ApplicationException;

    /**
     * Removes the record referenced by the key. Does nothing if key is null. Throws an exception if the key does not exist.
     */
    void remove(Long key) throws ApplicationException;

}
