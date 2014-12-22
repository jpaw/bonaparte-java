package de.jpaw.bonaparte.core;

public interface ExceptionConverter<E extends Exception> {
    public E enumExceptionConverter(IllegalArgumentException e) throws E;  // convert e to an exception of appropriate type. Also enrich it with current parser status
    public E customExceptionConverter(String msg, Exception e) throws E;   // create a custom parsing exception (to be used for type converters). Enrich with optional exception e
}
