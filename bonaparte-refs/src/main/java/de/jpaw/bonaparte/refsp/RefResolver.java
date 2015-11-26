package de.jpaw.bonaparte.refsp;

import java.util.List;

import de.jpaw.bonaparte.pojos.api.AbstractRef;
import de.jpaw.bonaparte.pojos.api.SearchFilter;
import de.jpaw.bonaparte.pojos.api.SortColumn;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.bonaparte.pojos.apip.DataWithTrackingP;
import de.jpaw.bonaparte.refs.BaseRefResolver;
import de.jpaw.util.ApplicationException;

/** API to noSQL backends (mini EntityManager) */
public interface RefResolver<REF extends AbstractRef, DTO extends REF, TRACKING extends TrackingBase> extends BaseRefResolver<REF, DTO, TRACKING> {
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
     * Removes the record referenced by the key. Does nothing if key = 0. Throws an exception if the key does not exist.
     */
    void remove(long key) throws ApplicationException;

    /** Returns a number of records for a query.
     * Throws UnsupportedOperationException in case the persistence provider does not support searches.
     */
    List<DataWithTrackingP<DTO,TRACKING>> query(int limit, int offset, SearchFilter filter, List<SortColumn> sortColumns) throws ApplicationException;

}
