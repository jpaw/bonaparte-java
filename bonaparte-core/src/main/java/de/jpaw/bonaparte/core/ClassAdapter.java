package de.jpaw.bonaparte.core;

public class ClassAdapter {

    public static String marshal(Class<?> obj) {
        return obj.getCanonicalName();
    }

    public static <E extends Exception> Class<?> unmarshal(String canonicalName, ExceptionConverter<E> p) throws E {
        try {
            return canonicalName == null ? null : Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw p.customExceptionConverter("Cannot find class " + canonicalName, e);
        }
    }
}
