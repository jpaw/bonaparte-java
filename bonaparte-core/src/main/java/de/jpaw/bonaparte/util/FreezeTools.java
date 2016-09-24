package de.jpaw.bonaparte.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.enums.EnumSetMarker;

/** Tools to return immutable or mutable copies of Json, Element and Array fields. */
public class FreezeTools {
    public static Map<String, Object> freeze(Map<String, Object> input) {
        if (input == null)
            return null;
        ImmutableMap.Builder<String, Object> b = ImmutableMap.<String, Object>builder();
        for (Map.Entry<String, Object> e : input.entrySet()) {
            Object obj = freeze(e.getValue());
            if (obj != null)
                b.put(e.getKey(), obj);
        }
        return b.build();
    }
    public static List<Object> freeze(List<Object> input) {
        if (input == null)
            return null;
        ImmutableList.Builder<Object> b = ImmutableList.<Object>builder();
        for (Object e : input) {
            Object obj = freeze(e);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    public static Set<Object> freeze(Set<Object> input) {
        if (input == null)
            return null;
        ImmutableSet.Builder<Object> b = ImmutableSet.<Object>builder();
        for (Object e : input) {
            Object obj = freeze(e);
            if (obj != null)
                b.add(obj);
        }
        return b.build();
    }
    
    @SuppressWarnings("unchecked")
    public static Object freeze(Object input) {
        if (input == null)
            return null;
        // for instance of Number, Instant, Boolean, String, Temporal etc. assume it is immutable
        // explicit action for BonaPortables, Maps, Lists, Sets
        if (input instanceof BonaPortable) {
            ((BonaPortable)input).freeze();
        } else if (input instanceof EnumSetMarker) {
            ((EnumSetMarker)input).freeze();
        } else if (input instanceof Map) {
            return freeze((Map<String, Object>)input);
        } else if (input instanceof List) {
            return freeze((List<Object>)input);
        } else if (input instanceof Set) {
            return freeze((Set<Object>)input);
        }
        // explicit
        return input;
    }
}
