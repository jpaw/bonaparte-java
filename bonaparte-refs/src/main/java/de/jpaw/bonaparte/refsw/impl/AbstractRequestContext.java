package de.jpaw.bonaparte.refsw.impl;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.api.PersistenceProviders;
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
    private static final int MAX_PERSISTENCE_PROVIDERS = PersistenceProviders.values().length;   // how many different persistence providers may participate?
    public static boolean AUTOCLOSE_ON_ROLLBACK_OR_COMMIT = true;           // could be adjusted by external parties

    public final String userId;
    public final String tenantId;
    public final Long userRef;
    public final Long tenantRef;
    public final long requestRef;
    public final Instant executionStart;     // to avoid checking the time repeatedly (takes 33 ns every time we do it), a timestamp is taken when the request processing starts

    private final PersistenceProvider [] persistenceUnits = new PersistenceProvider[MAX_PERSISTENCE_PROVIDERS];
    private int maxPersistenceProvider = -1;    // high water mark for the maximum index of a provider


    public AbstractRequestContext(Instant executionStart, String userId, String tenantId, Long userRef, Long tenantRef, long requestRef) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.tenantRef = tenantRef;
        this.userRef = userRef;
        this.requestRef = requestRef;
        this.executionStart = executionStart != null ? executionStart : Instant.now();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting RequestContext for user {}, tenant {}, processRef {}", userId, tenantId, Long.valueOf(requestRef));
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

    protected void closeAll(final boolean nullUnit) throws Exception {
        for (int i = 0; i <= maxPersistenceProvider; ++i) {
            if (persistenceUnits[i] != null) {
                persistenceUnits[i].close();
                if (nullUnit)
                    persistenceUnits[i] = null;
            }
        }
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
        if (AUTOCLOSE_ON_ROLLBACK_OR_COMMIT)
            closeAll(false);
    }

    @Override
    public void rollback() throws Exception {
        for (int i = 0; i <= maxPersistenceProvider; ++i)
            if (persistenceUnits[i] != null)
                persistenceUnits[i].rollback();
        if (AUTOCLOSE_ON_ROLLBACK_OR_COMMIT)
            closeAll(false);
    }

    @Override
    public void close() throws Exception {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Closing RequestContext for user {}, tenant {}, processRef {}", userId, tenantId, Long.valueOf(requestRef));

        closeAll(true);
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
    public long getRequestRef() {
        return requestRef;
    }

    @Override
    public Instant getExecutionStart() {
        return executionStart;
    }
}
