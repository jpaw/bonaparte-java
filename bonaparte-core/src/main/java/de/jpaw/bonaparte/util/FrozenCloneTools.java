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

public class FrozenCloneTools {
    public static Map<String, Object> frozenClone(Map<String, Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableMap.Builder<String, Object> b = ImmutableMap.<String, Object>builder();
        for (Map.Entry<String, Object> e : input.entrySet()) {
            Object obj = frozenClone(e.getValue());
            if (obj != null)
                b.put(e.getKey(), obj);
        }
        return b.build();
    }
    public static List<Object> frozenClone(List<Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableList.Builder<Object> b = ImmutableList.<Object>builder();
        for (Object e : input) {
            Object obj = frozenClone(e);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    public static Set<Object> frozenClone(Set<Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        ImmutableSet.Builder<Object> b = ImmutableSet.<Object>builder();
        for (Object e : input) {
            Object obj = frozenClone(e);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    
    @SuppressWarnings("unchecked")
    public static Object frozenClone(Object input) throws ObjectValidationException {
        if (input == null)
            return null;
        // for instance of Number, Instant, Boolean, String, Temporal etc. assume it is immutable
        // explicit action for BonaPortables, Maps, Lists, Sets
        if (input instanceof BonaPortable) {
            return ((BonaPortable)input).ret$FrozenClone();
        } else if (input instanceof EnumSetMarker) {
            return ((EnumSetMarker)input).ret$FrozenClone();
        } else if (input instanceof Map) {
            return frozenClone((Map<String, Object>)input);
        } else if (input instanceof List) {
            return frozenClone((List<Object>)input);
        } else if (input instanceof Set) {
            return frozenClone((Set<Object>)input);
        }
        // explicit
        return input;
    }
}
