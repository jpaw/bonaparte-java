package de.jpaw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.core.StringConverter;
import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** Sample implementation of StringConverter interface which replaces empty Strings with nulls */

public class StringConverterEmptyToNull implements StringConverter {

    @Override
    public String convert(String oldValue, final AlphanumericElementaryDataItem meta) {
        return (oldValue != null) && oldValue.isEmpty() ? null : oldValue;
    }

    @Override
    public List<String> convertList(List<String> oldList, final AlphanumericElementaryDataItem meta) {
        if (oldList == null)
            return null;
        List<String> newList = new ArrayList<String>(oldList.size());
        for (String s : oldList) {
            String newString = convert(s, meta);
            if (newString != null)
                newList.add(newString);
        }
        return (newList.isEmpty() && !meta.getIsRequired()) ? null : newList;  // only return null instead of an empty list if the List is required.
    }

    @Override
    public String[] convertArray(String[] oldArray, final AlphanumericElementaryDataItem meta) {
        if (oldArray == null)
            return null;
        for (int i = 0; i < oldArray.length; ++i)
            oldArray[i] = convert(oldArray[i], meta);
        return oldArray;  // no conversion of the array itself done here
    }

    @Override
    public <K> Map<K, String> convertMap(Map<K, String> oldMap, AlphanumericElementaryDataItem meta) {
        if (oldMap == null)
            return null;
        for (Map.Entry<K, String> i : oldMap.entrySet()) {
            oldMap.put(i.getKey(), convert(i.getValue(), meta));
        }
        return oldMap;
    }

}
