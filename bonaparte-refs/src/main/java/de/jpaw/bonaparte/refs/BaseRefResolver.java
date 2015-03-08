package de.jpaw.bonaparte.refs;

import java.util.List;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.api.DataWithTracking;
import de.jpaw.bonaparte.pojos.api.SearchFilter;
import de.jpaw.bonaparte.pojos.api.SortColumn;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.util.ApplicationException;

/** Common superinterface for refsp.RefResolver, refsw.RefResolver and refsc.RefResolver.
 * Defines common (not key type specific) methods. */
public interface BaseRefResolver<REF extends BonaPortable, DTO extends REF, TRACKING extends TrackingBase> {
    /**
     * Returns the DTO for a given unique index. Null-safe, returns null for a null index value. Throws an exception if the key does not exist.
     */
    DTO getDTO(REF refObject) throws ApplicationException;

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

    /**
     * Creates a key object from a ref. Returns null for ref <= 0.
     */
    REF createKey(long ref);

    /**
     * Creates a key object from a ref. Returns null for ref = null.
     * Similar method to previous, in order to avoid object allocation overhead.
     */
    REF createKey(Long ref);

    /** Returns a number of (technical) keys for a query.
     * In case of objects where no technical key of type Long can be determined, throws UnsupportedOperationException.
     * In this case, refsc will define an alternate query returning List<KEY>
     */
    List<Long> queryKeys(int limit, int offset, SearchFilter filter, List<SortColumn> sortColumns) throws ApplicationException;

    /** Returns a number of records for a query.
     * Throws UnsupportedOperationException in case the persistence provider does not support searches.
     */
    List<DataWithTracking<DTO,TRACKING>> query(int limit, int offset, SearchFilter filter, List<SortColumn> sortColumns) throws ApplicationException;
}
