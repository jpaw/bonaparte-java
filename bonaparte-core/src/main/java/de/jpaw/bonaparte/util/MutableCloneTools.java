package de.jpaw.bonaparte.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.enums.EnumSetMarker;

public class MutableCloneTools {
    public static Map<String, Object> mutableClone(Map<String, Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        Map<String, Object> b = new HashMap<String, Object>(input.size() * 2);  // reserve space for a load factor of .5
        for (Map.Entry<String, Object> e : input.entrySet()) {
            b.put(e.getKey(), mutableClone(e.getValue(), unfreezeCollections));
        }
        return b;
    }
    public static List<Object> mutableClone(List<Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        List<Object> b = new ArrayList<Object>(input.size());
        for (Object e : input) {
            b.add(mutableClone(e, unfreezeCollections));
        }
        return b;
    }
    public static Set<Object> mutableClone(Set<Object> input, boolean unfreezeCollections) throws ObjectValidationException {
        if (input == null)
            return null;
        Set<Object> b = new HashSet<Object>(input.size() * 2);
        for (Object e : input) {
            b.add(mutableClone(e, unfreezeCollections));
        }
        return b;
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
