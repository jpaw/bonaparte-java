package de.jpaw.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.core.DataConverter;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;

/** Base implementation of JAVATYPEConverter interface which offers array and nested object support */

public abstract class DataConverterAbstract<JAVATYPE,METATYPE extends FieldDefinition> implements DataConverter<JAVATYPE,METATYPE> {

    @Override
    public List<JAVATYPE> convertList(List<JAVATYPE> oldList, final METATYPE meta) {
        if (oldList == null)
            return null;
        List<JAVATYPE> newList = new ArrayList<JAVATYPE>(oldList.size());
        for (JAVATYPE s : oldList) {
            JAVATYPE newJAVATYPE = convert(s, meta);
            if (!meta.getIsRequired() || newJAVATYPE != null)     // only filter nulls if the target list has "required" fields
                newList.add(newJAVATYPE);
        }
        return (newList.isEmpty() && !meta.getIsAggregateRequired()) ? null : newList;  // only return null instead of an empty list if the List is required.
    }

    @Override
    public JAVATYPE[] convertArray(JAVATYPE[] oldArray, final METATYPE meta) {
        if (oldArray == null)
            return null;
        for (int i = 0; i < oldArray.length; ++i)
            oldArray[i] = convert(oldArray[i], meta);
        return oldArray;  // no conversion of the array itself done here
    }

    @Override
    public <K> Map<K, JAVATYPE> convertMap(Map<K, JAVATYPE> oldMap, METATYPE meta) {
        if (oldMap == null)
            return null;
        for (Map.Entry<K, JAVATYPE> i : oldMap.entrySet()) {
            oldMap.put(i.getKey(), convert(i.getValue(), meta));
        }
        return oldMap;
    }

    @Override
    public Set<JAVATYPE> convertSet(Set<JAVATYPE> oldSet, METATYPE meta) {
        if (oldSet == null)
            return null;
        Set<JAVATYPE> newSet = new HashSet<JAVATYPE>(oldSet.size());
        for (JAVATYPE i : oldSet) {
            newSet.add(convert(i, meta));
        }
        return newSet;
    }

}
