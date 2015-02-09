package de.jpaw.bonaparte.refsw.impl;

import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.refs.PersistenceProvider;
import de.jpaw.bonaparte.refsw.RequestContext;

/** Base implementation of some request's environment, usually enhanced by specific applications.
 * 
 * For every request, one of these is created.
 * Additional ones may be created for the asynchronous log writers (using dummy or null internalHeaderParameters)
 * 
 * Any functionality relating to customization has been moved to a separate class (separation of concerns).
 */
public class AbstractRequestContext implements RequestContext, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestContext.class);
    private static final int MAX_PERSISTENCE_PROVIDERS = 8;                     // how many different persistence providers may participate?
    
    public final String userId;
    public final String tenantId;
    public final Long userRef;
    public final Long tenantRef;
    public final Long requestRef;
    public final Instant executionStart;     // to avoid checking the time repeatedly (takes 33 ns every time we do it), a timestamp is taken when the request processing starts
    
    private final PersistenceProvider [] persistenceUnits = new PersistenceProvider[MAX_PERSISTENCE_PROVIDERS];
    private int maxPersistenceProvider = -1;    // high water mark for the maximum index of a provider

    
    public AbstractRequestContext(Instant executionStart, String userId, String tenantId, Long userRef, Long tenantRef, Long requestRef) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.tenantRef = tenantRef;
        this.userRef = userRef;
        this.requestRef = requestRef;
        this.executionStart = executionStart;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting RequestContext for user {}, tenant {}, processRef {}", userId, tenantId, requestRef);
    }
    
    // persistence services
    
    /** Informs the request context that the provider named participates in the transaction. */
    @Override
    public void addPersistenceContext(PersistenceProvider pprovider) {
        int ind = pprovider.getPriority();
        if (persistenceUnits[ind] == null) {
            persistenceUnits[ind] = pprovider;
            if (ind > maxPersistenceProvider)
                maxPersistenceProvider = ind;
            pprovider.open();                       // first time this request has seen it, open it!
        }
    }
    
    @Override
    public PersistenceProvider getPersistenceProvider(int priority) {
        return persistenceUnits[priority];
    }
    
    @Override
    public void commit() throws Exception {
        for (int i = 0; i <= maxPersistenceProvider; ++i) {
            try {
                if (persistenceUnits[i] != null)
                    persistenceUnits[i].commit();
            } catch (Exception e) {
                // if the commit fails, we have to roll back the others as well
                rollback();
                throw e;    // throw the original exception here (or convert to return code?)
            }
        }
        for (int i = 0; i <= maxPersistenceProvider; ++i)
            if (persistenceUnits[i] != null)
                persistenceUnits[i].close();
    }

    @Override
    public void rollback() throws Exception {
        for (int i = 0; i <= maxPersistenceProvider; ++i)
            if (persistenceUnits[i] != null)
                persistenceUnits[i].rollback();
        for (int i = 0; i <= maxPersistenceProvider; ++i)
            if (persistenceUnits[i] != null)
                persistenceUnits[i].close();
    }

    @Override
    public void close() throws Exception {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Closing RequestContext for user {}, tenant {}, processRef {}", userId, tenantId, requestRef);
        
        for (int i = 0; i <= maxPersistenceProvider; ++i) {
            if (persistenceUnits[i] != null) {
                persistenceUnits[i].close();
                // clear reference
                persistenceUnits[i] = null;
            }
        }
        maxPersistenceProvider = -1;
    }

    // standard getters as defined in the interface
    @Override
    public Long getTenantRef() {
        return tenantRef;
    }

    @Override
    public Long getUserRef() {
        return userRef;
    }

    @Override
    public Long getRequestRef() {
        return requestRef;
    }

    @Override
    public Instant getExecutionStart() {
        return executionStart;
    }
}
