package de.jpaw.bonaparte.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ObjectValidationException;
import de.jpaw.enums.EnumSetMarker;

public class FrozenCloneTools {
    public static Map<String, Object> frozenClone(Map<String, Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        Map<String, Object> b = new HashMap<String, Object>(FreezeTools.getInitialHashMapCapacity(input.size()));  // reserve space
        for (Map.Entry<String, Object> e : input.entrySet()) {
            b.put(e.getKey(), frozenClone(e.getValue()));
        }
        return Collections.unmodifiableMap(b);
    }
    public static List<Object> frozenClone(List<Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        List<Object> b = new ArrayList<Object>(input.size());
        for (Object e : input) {
            b.add(frozenClone(e));
        }
        return Collections.unmodifiableList(b);
    }
    public static Set<Object> frozenClone(Set<Object> input) throws ObjectValidationException {
        if (input == null)
            return null;
        Set<Object> b = new HashSet<Object>(input.size() * 2);
        for (Object e : input) {
            b.add(frozenClone(e));
        }
        return Collections.unmodifiableSet(b);
    }

    @SuppressWarnings("unchecked")
    public static Object frozenClone(Object input) throws ObjectValidationException {
        if (input == null)
            return null;
        // for instance of Number, Instant, Boolean, String, Temporal etc. assume it is immutable
        // explicit action for BonaPortables, Maps, Lists, Sets
        if (input instanceof BonaPortable b) {
            return b.ret$FrozenClone();
        } else if (input instanceof EnumSetMarker esm) {
            return esm.ret$FrozenClone();
        } else if (input instanceof Map m) {
            return frozenClone(m);
        } else if (input instanceof List l) {
            return frozenClone(l);
        } else if (input instanceof Set s) {
            return frozenClone(s);
        }
        // explicit
        return input;
    }
}
