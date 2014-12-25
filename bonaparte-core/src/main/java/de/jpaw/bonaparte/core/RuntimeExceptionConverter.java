package de.jpaw.bonaparte.core;

public class RuntimeExceptionConverter implements ExceptionConverter<RuntimeException> {
    public static RuntimeExceptionConverter INSTANCE = new RuntimeExceptionConverter();
    
    @Override
    public RuntimeException enumExceptionConverter(IllegalArgumentException e) {
        throw e;
    }

    @Override
    public RuntimeException customExceptionConverter(String msg, Exception e) {
        if (e == null)
            throw new RuntimeException(msg);
        else if (msg == null)
            throw new RuntimeException(e);
        else
            throw new RuntimeException(msg, e);
    }

}
