package de.jpaw.bonaparte.hazelcast.api;

import com.hazelcast.core.IMap
import com.hazelcast.query.PagingPredicate
import de.jpaw.bonaparte.pojos.api.SearchFilter
import de.jpaw.bonaparte.pojos.api.SortColumn
import de.jpaw.bonaparte.pojos.api.TrackingBase
import de.jpaw.bonaparte.pojos.apiw.DataWithTrackingW
import de.jpaw.bonaparte.pojos.apiw.Ref
import de.jpaw.bonaparte.refs.PersistenceException
import de.jpaw.bonaparte.refsw.RequestContext
import de.jpaw.bonaparte.refsw.TrackingUpdater
import de.jpaw.bonaparte.refsw.impl.AbstractRefResolver
import de.jpaw.dp.Inject
import de.jpaw.dp.Provider
import de.jpaw.util.ApplicationException
import java.util.List

/** Implementation of the RefResolver for hazelcast using an additional on heap near cache. */
abstract class AbstractHzBufferedRefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> extends AbstractRefResolver<REF, DTO, TRACKING> {

    @Inject        
    private HzCriteriaBuilder queryBuilder
    
    protected IMap<Long,DataWithTrackingW<DTO, TRACKING>> map;
    protected TrackingUpdater<TRACKING> trackingUpdater;
    protected Provider<RequestContext> contextProvider;
    protected String name;
    
    def abstract protected TRACKING createTracking();
    
    new(String name,
        IMap<Long, DataWithTrackingW<DTO, TRACKING>> map,
        TrackingUpdater<TRACKING> trackingUpdater,
        Provider<RequestContext> contextProvider
    ) {
        this.name = name
        this.map = map;
        this.trackingUpdater = trackingUpdater;
        this.contextProvider = contextProvider
    }
    
    override protected getUncached(Long key) {
        return map.get(key);
    }
    
    override protected getUncachedKey(REF refObject) throws PersistenceException {
        throw new UnsupportedOperationException("REF resolver not implemented")
        //throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
    }
    
    override protected uncachedCreate(DTO obj) throws PersistenceException {
        val dwt = new DataWithTrackingW(obj, createTracking, contextProvider.get().tenantRef)
        trackingUpdater.preCreate(contextProvider.get, dwt.tracking)
        if (map.put(obj.objectRef, dwt) !== null)
            throw new PersistenceException(PersistenceException.RECORD_ALREADY_EXISTS, obj.ref.longValue, name)
    }
    
    override protected uncachedRemove(DataWithTrackingW<DTO, TRACKING> previous) {
        map.remove(previous.data.objectRef)
    }
    
    override protected uncachedUpdate(DataWithTrackingW<DTO, TRACKING> dwt, DTO obj) throws PersistenceException {
        dwt.data = obj
        // update the tracking columns
        trackingUpdater.preUpdate(contextProvider.get, dwt.tracking)
        // write back the update
        map.set(obj.objectRef, dwt)
    }
    
    override createKey(long key) {
        return createKey(Long.valueOf(key));
    }
    
    override flush() {
    }
    
    override query(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
//        val predicate = hzFilter.applyFilter(new PredicateBuilder().entryObject, filters)
        val predicate = queryBuilder.buildPredicate(filters)  
        val limitedPredicate = if (limit == 0 || limit == Integer.MAX_VALUE) predicate else new PagingPredicate(predicate, limit)
        // TODO: call nextPage() as often as required in order to skip initial entries
        // TODO: ordering of results
        return map.values(limitedPredicate).toList
    }
    
    override queryKeys(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        val predicate = queryBuilder.buildPredicate(filters)  
        val limitedPredicate = if (limit == 0 || limit == Integer.MAX_VALUE) predicate else new PagingPredicate(predicate, limit)
        // TODO: call nextPage() as often as required in order to skip initial entries
        // TODO: ordering of results
        return map.keySet(limitedPredicate).toList
    }    
}
