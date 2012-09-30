package de.jpaw.bonaparte.core;

public interface BufferedMessageComposer<E extends Exception> extends MessageComposer<E> {
    // generic methods
    public void reset();       // restart the output
    public int getLength();    // obtain the number of written bytes (composer)
    public byte[] getBuffer(); // get the buffer (byte array of maybe too big size
    public byte[] getBytes();  // get exact byte array of produced output
}
