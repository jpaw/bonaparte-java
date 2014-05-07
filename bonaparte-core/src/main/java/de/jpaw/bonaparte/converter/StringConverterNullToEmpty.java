package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** This implementation inherits the array / list processing methods from StringConverterAbstract and only replaces the scalar function
 * and acts the opposite way, i.e. replaces nulls with empty Strings. */
public class StringConverterNullToEmpty extends StringConverterAbstract {
    static private final String EMPTY_STRING = "";

    @Override
    public String convert(String oldValue, final AlphanumericElementaryDataItem meta) {
        return oldValue == null ? EMPTY_STRING : oldValue;
    }

}
