package de.jpaw.bonaparte.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.enums.EnumSetMarker;

/** Tools to return immutable or mutable copies of Json, Element and Array fields. */
public class FreezeTools {
    public static Map<String, Object> freeze(Map<String, Object> input) {
        if (input == null)
            return null;
        Map<String, Object> b = new HashMap<String, Object>(input.size());  // reserve space for a load factor of .5
        for (Map.Entry<String, Object> e : input.entrySet()) {
            b.put(e.getKey(), freeze(e.getValue()));
        }
        return Collections.unmodifiableMap(b);
    }
    public static List<Object> freeze(List<Object> input) {
        if (input == null)
            return null;
        List<Object> b = new ArrayList<Object>(input.size());
        for (Object e : input) {
            b.add(freeze(e));
        }
        return Collections.unmodifiableList(b);
    }
    public static Set<Object> freeze(Set<Object> input) {
        if (input == null)
            return null;
        Set<Object> b = new HashSet<Object>(input.size() * 2);
        for (Object e : input) {
            b.add(freeze(e));
        }
        return Collections.unmodifiableSet(b);
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
