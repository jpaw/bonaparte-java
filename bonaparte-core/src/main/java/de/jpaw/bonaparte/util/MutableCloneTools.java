package de.jpaw.bonaparte.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.enums.EnumSetMarker;

public class MutableCloneTools {
    public static Map<String, Object> mutableClone(Map<String, Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableMap.Builder<String, Object> b = ImmutableMap.<String, Object>builder();
        for (Map.Entry<String, Object> e : input.entrySet()) {
            Object obj = mutableClone(e.getValue(), unfreezeCollections);
            if (obj != null)
                b.put(e.getKey(), obj);
        }
        return b.build();
    }
    public static List<Object> mutableClone(List<Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableList.Builder<Object> b = ImmutableList.<Object>builder();
        for (Object e : input) {
            Object obj = mutableClone(e, unfreezeCollections);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    public static Set<Object> mutableClone(Set<Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableSet.Builder<Object> b = ImmutableSet.<Object>builder();
        for (Object e : input) {
            Object obj = mutableClone(e, unfreezeCollections);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    
    @SuppressWarnings("unchecked")
    public static Object mutableClone(Object input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        // for instance of Number, Instant, Boolean, String, Temporal etc. assume it is immutable
        // explicit action for BonaPortables, Maps, Lists, Sets
        if (input instanceof BonaPortable) {
            return ((BonaPortable)input).ret$MutableClone(true, unfreezeCollections);
        } else if (input instanceof EnumSetMarker) {
            return ((EnumSetMarker)input).ret$MutableClone(true, unfreezeCollections);
        } else if (input instanceof Map) {
            return mutableClone((Map<String, Object>)input, unfreezeCollections);
        } else if (input instanceof List) {
            return mutableClone((List<Object>)input, unfreezeCollections);
        } else if (input instanceof Set) {
            return mutableClone((Set<Object>)input, unfreezeCollections);
        }
        // explicit
        return input;
    }
}
