package de.jpaw.bonaparte.ehcache3.api

import de.jpaw.bonaparte.pojos.apiw.DataWithTrackingW
import de.jpaw.bonaparte.pojos.api.SearchFilter
import de.jpaw.bonaparte.pojos.api.SortColumn
import de.jpaw.bonaparte.pojos.api.TrackingBase
import de.jpaw.bonaparte.pojos.apiw.Ref
import de.jpaw.bonaparte.refs.PersistenceException
import de.jpaw.bonaparte.refsw.RefResolver
import de.jpaw.bonaparte.refsw.RequestContext
import de.jpaw.bonaparte.refsw.TrackingUpdater
import de.jpaw.dp.Provider
import de.jpaw.util.ApplicationException
import java.util.List
import org.ehcache.Cache

/** Implementation of the RefResolver for ehCache / terracotta without an additional on heap near cache. */
abstract class AbstractEhcUnbufferedRefResolver<REF extends Ref, DTO extends REF, TRACKING extends TrackingBase> implements RefResolver<REF, DTO, TRACKING> {

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

    override getDTO(Long key) throws ApplicationException {
        if (key === null)
            return null
        val dwt = map.get(key)
        if (dwt !== null)
            return dwt.data
        throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
    }

    override getTracking(Long key) throws ApplicationException {
        val entry = map.get(key)
        if (entry === null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
        return entry.tracking
    }

    override remove(Long key) throws ApplicationException {
        map.remove(key)
    }

    override create(DTO dto) throws ApplicationException {
        val dwt = new DataWithTrackingW(dto, createTracking, contextProvider.get().tenantRef)
        trackingUpdater.preCreate(contextProvider.get, dwt.tracking)
        map.put(dto.objectRef, dwt)
    }

    override createKey(long key) {
        return createKey(Long.valueOf(key));
    }

    override getDTO(REF ref) throws ApplicationException {
        if (ref === null)
            return null
        val key = getRef(ref)
        val dwt = map.get(key)
        if (dwt === null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, key.longValue, name)
        return dwt.data
    }

    override update(DTO newDTO) throws ApplicationException {
        // read the previous recorded data (mainly to get the tracking information)
        val dwt = map.get(newDTO.ref)
        if (dwt === null)
            throw new PersistenceException(PersistenceException.RECORD_DOES_NOT_EXIST, newDTO.ref.longValue, name)
        dwt.data = newDTO
        // update the tracking columns
        trackingUpdater.preUpdate(contextProvider.get, dwt.tracking)
        // write back the update
        map.put(newDTO.objectRef, dwt)
    }

    // no local cache - nothing to to
    override final clear() {
    }

    override flush() {
    }

    override query(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        throw new UnsupportedOperationException();
    }

    override queryKeys(int limit, int offset, SearchFilter filters, List<SortColumn> sortColumns) throws ApplicationException {
        throw new UnsupportedOperationException();
    }
}
