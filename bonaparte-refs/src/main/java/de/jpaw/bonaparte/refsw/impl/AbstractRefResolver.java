package de.jpaw.bonaparte.refsw.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.api.AbstractRef;
import de.jpaw.bonaparte.pojos.api.DataWithTracking;
import de.jpaw.bonaparte.pojos.api.SearchFilter;
import de.jpaw.bonaparte.pojos.api.SortColumn;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.bonaparte.refs.PersistenceException;
import de.jpaw.bonaparte.refsw.RefResolver;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteBuilder;

//TODO FIXME:   check nochange columns in update method

/**
 * An abstract class which implements the common functionality of a RefResolver for off heap key value stores. The topics are:
 *
 * The first topic is operation of a first level cache (on heap) for data objects. Similar to the JPA entity manager, its task is to provide a unique identity
 * for subsequent queries to the same object within a single transaction. It also improves read performance when the data object is of significant size, because
 * no repeated deserializations have to be done. No caching is performed on index values, because the index is assumed to be small and the overhead of cache
 * operation may be higher than actual updates or lookups itself.
 *
 * The second aspect is the maintenance of change tracking fields for audit purposes. The tracking fields are available in read/write mode to the application,
 * most operations work on the business fields only (DTO). Tracking data is provided upon request, and in that case, a read-only copy is created and handed
 * back.
 *
 * @author Michael Bischoff
 *
 * @param <REF>
 * @param <DTO>
 * @param <TRACKING>
 */
public abstract class AbstractRefResolver<REF extends AbstractRef, DTO extends REF, TRACKING extends TrackingBase> implements RefResolver<REF, DTO, TRACKING> {
    private ConcurrentMap<Long,DataWithTracking<DTO, TRACKING>> cache = new ConcurrentHashMap<Long, DataWithTracking<DTO, TRACKING>>(1024 * 1024);

    protected ByteBuilder builder;
    protected String entityName;

    protected int indexHash(int off) {
        int hash = 1;
        final byte[] buffer = builder.getCurrentBuffer();
        while (off < builder.length()) {
            hash = 31 * hash + buffer[off++];
        }
        return hash;
    }

    /** Look up a primary key by some unique index. */
    protected abstract Long getUncachedKey(REF refObject) throws PersistenceException;

    /** Return an object stored in the DB by its primary key. */
    protected abstract DataWithTracking<DTO, TRACKING> getUncached(Long ref);

    /** Update some object fwt to have obj as the data portion. (Update tracking and then update the DB and possibly indexes.) */
    protected abstract void uncachedUpdate(DataWithTracking<DTO, TRACKING> dwt, DTO obj) throws PersistenceException;

    /** Removes an object if it exists. */
    protected abstract void uncachedRemove(DataWithTracking<DTO, TRACKING> previous);

    /** Create some object. Returns the object including tracking data, or throws an exception, if the object already exists. */
    protected abstract DataWithTracking<DTO, TRACKING> uncachedCreate(DTO obj) throws PersistenceException;

    @Override
    public final Long getRef(REF refObject) throws PersistenceException {
        if (refObject == null)
            return null;
        Long key = refObject.ret$RefW();
        if (key != null)
            return key;
        // shortcuts not possible, try the local reverse cache
        // key = indexCache.get(refObject);
        // if (key > 0)
        // return key;
        // not in cache either, consult second level (in-memory DB)
        key = getUncachedKey(refObject);
        if (key == null)
            throw new PersistenceException(PersistenceException.NO_RECORD_FOR_INDEX, 0L, entityName, refObject.ret$PQON(), refObject.toString());
        // if (key > 0)
        // indexCache.put(refObject, key);
        return key;
    }

    /** return data for a key. Returns null if no record exists. */
    protected final DataWithTracking<DTO, TRACKING> getDTONoCacheUpd(Long ref) {
        // first, try to retrieve a value from the cache, in order to be identity-safe
        DataWithTracking<DTO, TRACKING> value = cache.get(ref);
        if (value != null)
            return value;
        // not here, consult second level (in-memory DB)
        return getUncached(ref);
    }

    @Override
    public final DTO getDTO(REF refObject) throws PersistenceException {
        if (refObject == null)
            return null;
        return getDTO(getRef(refObject));
    }

    @Override
    public final DTO getDTO(Long ref) throws PersistenceException {
        if (ref == null)
            return null;
        DataWithTracking<DTO, TRACKING> value = getDTONoCacheUpd(ref);
        if (value != null) {
            cache.put(ref, value);
            return value.getData();
        } else {
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, ref.longValue(), entityName);
        }
    }

    @Override
    public final void update(DTO obj) throws PersistenceException {
        Long key = obj.ret$RefW();
        if (key == null)
            throw new PersistenceException(PersistenceException.NO_PRIMARY_KEY, 0L, entityName);
        DataWithTracking<DTO, TRACKING> dwt = getDTONoCacheUpd(key);
        if (dwt == null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key, entityName);
        uncachedUpdate(dwt, obj);
        // it's already in the cache, and the umbrella object hasn't changed, so no cache update required
    }

    @Override
    public final void remove(Long key) throws PersistenceException {
        DataWithTracking<DTO, TRACKING> value = getDTONoCacheUpd(key);
        if (value != null) {
            // must remove it
            cache.remove(key);
            uncachedRemove(value);
        }
    }

    @Override
    public void create(DTO obj) throws PersistenceException {
        if (obj.ret$RefW() == null)
            throw new PersistenceException(PersistenceException.NO_PRIMARY_KEY, 0L, entityName);
        DataWithTracking<DTO, TRACKING> dwt = uncachedCreate(obj);
        cache.put(obj.ret$RefW(), dwt);
    }

    @Override
    public TRACKING getTracking(Long ref) throws PersistenceException {
        DataWithTracking<DTO, TRACKING> dwt = getDTONoCacheUpd(ref);
        if (dwt == null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, ref, entityName);
        try {
            return (TRACKING) dwt.getTracking().ret$FrozenClone();
        } catch (ObjectValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /** Clears the cache (but not any underlying data storage!).
     * Used to achieve transaction based caching. */
    @Override
    public final void clear() {
        cache.clear();
    }

    @Override
    public List<Long> queryKeys(int limit, int offset, SearchFilter filter, List<SortColumn> sortColumns) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DataWithTracking<DTO, TRACKING>> query(int limit, int offset, SearchFilter filter, List<SortColumn> sortColumns)
            throws ApplicationException {
        throw new UnsupportedOperationException();
    }
}
