package de.jpaw.bonaparte.util;

/** Method to be called when a deprecated field is accessed. */
public interface IDeprecationWarner {
    void warn(Object obj, String fieldName);
}
