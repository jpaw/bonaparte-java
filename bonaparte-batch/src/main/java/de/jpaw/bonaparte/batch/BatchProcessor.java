package de.jpaw.bonaparte.batch;

public interface BatchProcessor<E,F> {
	F process(int recordNo, E data) throws Exception;
	void close() throws Exception;
}
