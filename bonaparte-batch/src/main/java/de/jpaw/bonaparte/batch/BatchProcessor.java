package de.jpaw.bonaparte.batch;

/** Process input of type E to produce output of type F. */
public interface BatchProcessor<E,F> {
    F process(int recordNo, E data) throws Exception;
    void close() throws Exception;
}
