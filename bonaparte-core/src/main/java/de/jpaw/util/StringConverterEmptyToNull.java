package de.jpaw.util;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** Sample implementation of StringConverter interface which replaces empty Strings with nulls */

public class StringConverterEmptyToNull extends StringConverterAbstract {

    @Override
    public String convert(String oldValue, final AlphanumericElementaryDataItem meta) {
        return (oldValue != null) && oldValue.isEmpty() ? null : oldValue;
    }

}
