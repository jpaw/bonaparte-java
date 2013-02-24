package de.jpaw.util;

/** This implementation inherits the array / list processing methods from StringConverterEmptyToNull and only replaces the scalar function
 * and acts the opposite way, i.e. replaces nulls with empty Strings. */
public class StringConverterNullToEmpty extends StringConverterEmptyToNull {
    static private final String EMPTY_STRING = "";

    @Override
    public String convert(String oldValue, int maxLength, boolean isRequired) {
        return oldValue == null ? EMPTY_STRING : oldValue;
    }

}
