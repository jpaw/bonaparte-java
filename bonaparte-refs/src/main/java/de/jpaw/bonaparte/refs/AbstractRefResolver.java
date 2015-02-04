package de.jpaw.bonaparte.refs;

import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.bonaparte.pojos.api.DataWithTracking;
import de.jpaw.bonaparte.pojos.api.Ref;
import de.jpaw.bonaparte.pojos.api.TrackingBase;
import de.jpaw.primitivecollections.HashMapPrimitiveLongObject;
import de.jpaw.util.ByteBuilder;

//TODO FIXME:   check nochange columns in update method

public abstract class AbstractRefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> implements RefResolver<REF, DTO, TRACKING> {
    private HashMapPrimitiveLongObject<DataWithTracking<DTO, TRACKING>> cache;
    // protected HashMapObjectPrimitiveLong<REF> indexCache;

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
    protected abstract long getUncachedKey(REF refObject) throws PersistenceException;

    /** Return an object stored in the DB by its primary key. */
    protected abstract DataWithTracking<DTO, TRACKING> getUncached(long ref);

    /** Update some object fwt to have obj as the data portion. (Update tracking and then update the DB and possibly indexes.) */
    protected abstract void uncachedUpdate(DataWithTracking<DTO, TRACKING> dwt, DTO obj) throws PersistenceException;

    /** Removes an object if it exists. */
    protected abstract void uncachedRemove(DataWithTracking<DTO, TRACKING> previous);

    /** Create some object. Returns the object including tracking data, or throws an exception, if the object already exists. */
    protected abstract DataWithTracking<DTO, TRACKING> uncachedCreate(DTO obj) throws PersistenceException;

    @Override
    public final long getRef(REF refObject) throws PersistenceException {
        if (refObject == null)
            return 0;
        long key = refObject.getObjectRef();
        if (key > 0)
            return key;
        // shortcuts not possible, try the local reverse cache
        // key = indexCache.get(refObject);
        // if (key > 0)
        // return key;
        // not in cache either, consult second level (in-memory DB)
        key = getUncachedKey(refObject);
        // if (key > 0)
        // indexCache.put(refObject, key);
        return key;
    }

    protected final DataWithTracking<DTO, TRACKING> getDTONoCacheUpd(long ref) {
        // first, try to retrieve a value from the cache, in order to be identity-safe
        DataWithTracking<DTO, TRACKING> value = cache.get(ref);
        if (value != null)
            return value;
        // not here, consult second level (in-memory DB)
        value = getUncached(ref);
        return value;
    }

    @Override
    public final DTO getDTO(REF refObject) throws PersistenceException {
        if (refObject == null)
            return null;
        return getDTO(getRef(refObject));
    }

    @Override
    public final DTO getDTO(long ref) {
        if (ref <= 0L)
            return null;
        DataWithTracking<DTO, TRACKING> value = getDTONoCacheUpd(ref);
        if (value != null) {
            cache.put(ref, value);
            return value.getDto();
        }
        return null;
    }

    @Override
    public final void update(DTO obj) throws PersistenceException {
        if (obj.getObjectRef() <= 0)
            throw new PersistenceException(PersistenceException.NO_PRIMARY_KEY, 0L, entityName);
        long key = obj.getObjectRef();
        DataWithTracking<DTO, TRACKING> dwt = getDTONoCacheUpd(key);
        if (dwt == null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key, entityName);
        uncachedUpdate(dwt, obj);
        // it's already in the cache, and the umbrella object hasn't changed, so no cache update required
    }

    @Override
    public final void remove(long key) throws PersistenceException {
        DataWithTracking<DTO, TRACKING> value = getDTONoCacheUpd(key);
        if (value != null) {
            // must remove it
            cache.remove(key);
            uncachedRemove(value);
        }
    }

    @Override
    public void create(DTO obj) throws PersistenceException {
        if (obj.getObjectRef() <= 0)
            throw new PersistenceException(PersistenceException.NO_PRIMARY_KEY, 0L, entityName);
        DataWithTracking<DTO, TRACKING> dwt = uncachedCreate(obj);
        cache.put(obj.getObjectRef(), dwt);
    }

    @Override
    public TRACKING getTracking(long ref) throws PersistenceException {
        DataWithTracking<DTO, TRACKING> dwt = getDTONoCacheUpd(ref);
        if (dwt == null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, ref, entityName);
        try {
            return (TRACKING) dwt.getTracking().get$FrozenClone();
        } catch (ObjectValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final void clear() {
        cache.clear();
    }
}
