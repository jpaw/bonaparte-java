package de.jpaw.bonaparte.core;

/** Client facing part of an interface for MessageComposers working on memory buffers. */
public interface BufferedMessageWriter<E extends Exception> extends MessageWriter<E> {
    // generic methods
    public void reset();       // restart the output
    public int getLength();    // obtain the number of written bytes (composer)
    public byte[] getBuffer(); // get the buffer (byte array of maybe too big size
    public byte[] getBytes();  // get exact byte array of produced output
}
