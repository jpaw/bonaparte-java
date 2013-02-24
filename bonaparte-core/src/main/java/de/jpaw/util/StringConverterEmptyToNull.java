package de.jpaw.util;

import java.util.ArrayList;
import java.util.List;

import de.jpaw.bonaparte.core.StringConverter;

/** Sample implementation of StringConverter interface which replaces empty Strings with nulls */

public class StringConverterEmptyToNull implements StringConverter {

    @Override
    public String convert(String oldValue, int maxLength, boolean isRequired) {
        return (oldValue != null) && oldValue.isEmpty() ? null : oldValue;
    }

    @Override
    public List<String> convertList(List<String> oldList, int maxElementLength, int maxElements, boolean isRequired) {
        if (oldList == null)
            return null;
        List<String> newList = new ArrayList<String>(oldList.size());
        for (String s : oldList) {
            String newString = convert(s, maxElementLength, isRequired);
            if (newString != null)
                newList.add(newString);
        }
        return (newList.isEmpty() && !isRequired) ? null : newList;  // only return null instead of an empty list if the List is required.
    }

    @Override
    public String[] convertArray(String[] oldArray, int maxElementLength, int maxElements, boolean isRequired) {
        for (int i = 0; i < oldArray.length; ++i)
            oldArray[i] = convert(oldArray[i], maxElementLength, isRequired);
        return oldArray;  // no conversion of the array itself done here
    }

}
