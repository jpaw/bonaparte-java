package de.jpaw.bonaparte.batch;

/** Represents a generic data record with an integral ordinal attached to it. */
public class DataWithOrdinal<E> {
    public int recordno;                // just a counter 1...n
    public E data;                      // the actual payload
    
    public DataWithOrdinal(int n, E data) {
        this.recordno = n;
        this.data = data;
    }
}
