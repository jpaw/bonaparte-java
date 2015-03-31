package de.jpaw.bonaparte.ehcache.api;

import de.jpaw.bonaparte.pojos.api.DataWithTracking
import de.jpaw.bonaparte.pojos.api.SearchFilter
import de.jpaw.bonaparte.pojos.api.SortColumn
import de.jpaw.bonaparte.pojos.api.TrackingBase
import de.jpaw.bonaparte.pojos.apiw.Ref
import de.jpaw.bonaparte.refs.PersistenceException
import de.jpaw.bonaparte.refsw.RefResolver
import de.jpaw.bonaparte.refsw.RequestContext
import de.jpaw.bonaparte.refsw.TrackingUpdater
import de.jpaw.dp.Inject
import de.jpaw.dp.Provider
import de.jpaw.util.ApplicationException
import java.util.List
import net.sf.ehcache.Cache
import net.sf.ehcache.search.Attribute
import net.sf.ehcache.search.Direction
import net.sf.ehcache.Element

/** Implementation of the RefResolver for ehCache / terracotta without an additional on heap near cache. */
abstract class AbstractEhcUnbufferedRefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> implements RefResolver<REF, DTO, TRACKING> {
        
    @Inject        
    private EhcCriteriaBuilder queryBuilder
    
    protected Cache map;
    protected TrackingUpdater<TRACKING> trackingUpdater;
    protected Provider<RequestContext> contextProvider;
    protected String name;
    
    def abstract protected TRACKING createTracking();
    
    // override me to provide sortable column resolution
    def protected Attribute<?> getAttributeByName(String name) {
        return null
    }
    
    new(String name,
        Cache map,
        TrackingUpdater<TRACKING> trackingUpdater,
        Provider<RequestContext> contextProvider
    ) {
        this.name = name
        this.map = map;
        this.trackingUpdater = trackingUpdater;
        this.contextProvider = contextProvider
    }
    
    override getDTO(Long key) throws ApplicationException {
        val entry = map.get(key)
        return (entry?.objectValue as DataWithTracking<DTO, TRACKING>).data
    }
    
    override getTracking(Long key) throws ApplicationException {
        val entry = map.get(key)
        return (entry?.objectValue as DataWithTracking<DTO, TRACKING>).tracking
    }
    
    override remove(Long key) throws ApplicationException {
        map.remove(key)
    }
    
    override create(DTO dto) throws ApplicationException {
        val dwt = new DataWithTracking(dto, createTracking)
        trackingUpdater.preCreate(contextProvider.get, dwt.tracking)
        map.put(new Element(dto.objectRef, dwt))
    }
    
    override createKey(long key) {
        return createKey(Long.valueOf(key));
    }
    
    override getDTO(REF ref) throws ApplicationException {
        val key = getRef(ref)
        val dwt = map.get(key)
        if (dwt === null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
        return (dwt.objectValue as DataWithTracking<DTO, TRACKING>).data
    }
    
    override update(DTO newDTO) throws ApplicationException {
        // read the previous recorded data (mainly to get the tracking information)
        val e = map.get(newDTO.ref)
        if (e === null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, newDTO.ref.longValue, name)
        val dwt = e.objectValue as DataWithTracking<DTO, TRACKING>
        dwt.data = newDTO
        // update the tracking columns
        trackingUpdater.preUpdate(contextProvider.get, dwt.tracking)
        // write back the update
        map.put(new Element(newDTO.objectRef, dwt))
    }
    
    // no local cache - nothing to to
    override final clear() {
    }
    
    override flush() {
    }
    
    // common subroutine for query and queryKeys
    def protected querySub(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        val criteria = queryBuilder.buildPredicate(map, filters)  
        // 
        val query = map.createQuery
        if (criteria !== null)
            query.addCriteria(criteria)
        if (sortColumns !== null && sortColumns.size >= 1) {
            val sort = sortColumns.get(0)
            val attr = getAttributeByName(sort.fieldName)
            if (attr !== null)
                query.addOrderBy(attr, if (sort.descending) Direction.DESCENDING else Direction.ASCENDING)
        }
        if (limit > 0 && limit < Integer.MAX_VALUE)
            query.maxResults(limit + offset)
        return query
    }
    
    override query(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        val results = querySub(limit, offset, filters, sortColumns).includeValues.execute
        val resultSet = if (offset == 0) results.all else results.range(offset, limit)   
        val r = resultSet.map[value as DataWithTracking<DTO, TRACKING>].toList
        results.discard  // free memory
        return r
    }
    
    override queryKeys(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        val results = querySub(limit, offset, filters, sortColumns).includeKeys.execute
        val resultSet = if (offset == 0) results.all else results.range(offset, limit)   
        val r = resultSet.map[key as Long].toList
        results.discard  // free memory
        return r
    }    
}
