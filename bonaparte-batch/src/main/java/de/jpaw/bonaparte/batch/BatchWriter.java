package de.jpaw.bonaparte.batch;

/** Defines the methods a bonaparte output writer must implement.
 * The store() method is called in ordered or unordered sequence for every processed record.
 * If processing resulted in an exception, the data component of response will be null.
 * A single thread (or the main thread) will be allocated to writing.
 * 
 * The writer should make use of the Contributor interface to obtain parameters like filename and such.
 */

public interface BatchWriter<F> extends Contributor {
	public void storeResult(int recordno, F response) throws Exception;
}
