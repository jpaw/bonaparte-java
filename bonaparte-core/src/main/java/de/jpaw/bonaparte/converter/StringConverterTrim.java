package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** Sample implementation of StringConverter interface which trims Strings. */

public class StringConverterTrim extends StringConverterAbstract {

    @Override
    public String convert(String oldValue, final AlphanumericElementaryDataItem meta) {
        return (oldValue != null) ? oldValue.trim() : oldValue;
    }

}
