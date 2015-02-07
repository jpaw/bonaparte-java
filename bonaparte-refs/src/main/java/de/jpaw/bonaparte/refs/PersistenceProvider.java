package de.jpaw.bonaparte.refs;

/**
 * API provided by persistence implementations like JPA, in-memory storage, noSQL providers...
 * 
 * PersistenceProviders register at the RequestContext in order to get their commit / rollback callback invoked at the end of the request.
 * */
public interface PersistenceProvider extends AutoCloseable {
    /** Returns some identification of the provider. (PersistenceProviders.name()) */
    public String getId();

    /** Returns the numeric ID for the provider. (PersistenceProviders.ordinal()) */
    public int getPriority();

    /** Starts a transaction. */
    public void open();

    /** Rolls back the current transaction. */
    public void rollback();

    /** Commits the current transaction. */
    public void commit() throws Exception;

    /** Closes the context, should be preceeded by commit() or rollback(). */
    @Override
    public void close() throws Exception;
}
