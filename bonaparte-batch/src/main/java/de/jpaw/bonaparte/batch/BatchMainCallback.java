package de.jpaw.bonaparte.batch;

/** Callback which an input source has to call per record.
 * 
 */
public interface BatchMainCallback<E> {
	void scheduleForProcessing(E record) throws Exception;
}
