package de.jpaw.bonaparte.core;

public interface BufferedMessageComposer<E extends Exception> extends MessageComposer<E>, BufferedMessageWriter<E> {}
