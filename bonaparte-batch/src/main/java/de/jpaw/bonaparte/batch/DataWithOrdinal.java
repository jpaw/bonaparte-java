package de.jpaw.bonaparte.batch;

/** Represents a generic data record with an integral ordinal attached to it. */
public class DataWithOrdinal<E> {
	public Integer  recordno;			// just a counter 1...n
	public E data;						// the actual payload
	
	public DataWithOrdinal(Integer n, E data) {
		recordno = n;
		this.data = data;
	}
}
