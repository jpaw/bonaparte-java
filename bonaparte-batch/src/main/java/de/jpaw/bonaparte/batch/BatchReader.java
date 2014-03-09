package de.jpaw.bonaparte.batch;

/** Defines the methods a bonaparte batch processor must implement.
 * The implementation typically also hosts the main() method, and invokes the batch processor
 * with a reference of an instance to itself.
 *
 */

public interface BatchReader<E> extends Contributor {
	public void produceTo(BatchMainCallback<E> whereToPut) throws Exception;
}
