package de.jpaw.bonaparte.refsp;

import org.joda.time.Instant;

import de.jpaw.bonaparte.refs.PersistenceProvider;

/** Implementations provide information about the current request context, such as user, tenant, timestamp.
 * The implementing class is either some static information provider, some ThreadLocal, or some CDI injected instance. */
public interface RequestContext extends AutoCloseable {
    /** Returns some identification about the current tenant in multi-tenant environments, or 0 if not applicable. */
    long getTenantRef();
    /** Returns some identification of the current user, or 0 if no user has been authenticated. */ 
    long getUserRef();
    /** Returns some identification about the request being processed, or 0 if not applicable. */ 
    long getRequestRef();
    /** Returns cached information when the request processing has started (to avoid repeatedly querying the system clock, which is a costly operation). */ 
    Instant getExecutionStart();
    
    /** Retrieves the persistence provider of the given priority. */
    public PersistenceProvider getPersistenceProvider(int priority);
    /** Adds a persistence provider to the list of involved transaction participants. */
    void addPersistenceContext(PersistenceProvider pprovider);
    
    /** Commits the current transaction. Issues a commit to all registered persistence providers, in order of ascending priority. */
    void commit() throws Exception;
    /** Rolls back the current transaction. Issues the rollback to all registered persistence providers, in order of ascending priority. */
    void rollback() throws Exception;
}
