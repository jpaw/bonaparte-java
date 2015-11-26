package de.jpaw.bonaparte.ehcache3.api;

import de.jpaw.bonaparte.pojos.apiw.DataWithTrackingW
import de.jpaw.bonaparte.pojos.api.TrackingBase
import de.jpaw.bonaparte.pojos.apiw.Ref
import de.jpaw.bonaparte.refs.PersistenceException
import de.jpaw.bonaparte.refsw.RequestContext
import de.jpaw.bonaparte.refsw.TrackingUpdater
import de.jpaw.bonaparte.refsw.impl.AbstractRefResolver
import de.jpaw.dp.Provider
import org.ehcache.Cache

/** Implementation of the RefResolver for ehCache / terracotta using an additional on heap near cache. */
abstract class AbstractEhcBufferedRefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> extends AbstractRefResolver<REF, DTO, TRACKING> {
        
    protected Cache<Long, DataWithTrackingW<DTO, TRACKING>> map;
    protected TrackingUpdater<TRACKING> trackingUpdater;
    protected Provider<RequestContext> contextProvider;
    protected String name;
    
    def abstract protected TRACKING createTracking();
    
    new(String name,
        Cache<Long, DataWithTrackingW<DTO, TRACKING>> map,
        TrackingUpdater<TRACKING> trackingUpdater,
        Provider<RequestContext> contextProvider
    ) {
        this.name = name
        this.map = map;
        this.trackingUpdater = trackingUpdater;
        this.contextProvider = contextProvider
    }
    
    override protected getUncached(Long key) {
        return map.get(key)
    }
    
    override protected getUncachedKey(REF refObject) throws PersistenceException {
        throw new UnsupportedOperationException("REF resolver not implemented")
        //throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
    }
    
    override protected uncachedCreate(DTO obj) throws PersistenceException {
        val dwt = new DataWithTrackingW(obj, createTracking, contextProvider.get().tenantRef)
        trackingUpdater.preCreate(contextProvider.get, dwt.tracking)
        map.put(obj.objectRef, dwt)
        return dwt
    }
    
    override protected uncachedRemove(DataWithTrackingW<DTO, TRACKING> previous) {
        map.remove(previous.data.objectRef)
    }
    
    override protected uncachedUpdate(DataWithTrackingW<DTO, TRACKING> dwt, DTO obj) throws PersistenceException {
        dwt.data = obj
        // update the tracking columns
        trackingUpdater.preUpdate(contextProvider.get, dwt.tracking)
        // write back the update
        map.put(obj.objectRef, dwt)
    }
    
    override createKey(long key) {
        return createKey(Long.valueOf(key));
    }
    
    override flush() {
    }
}
