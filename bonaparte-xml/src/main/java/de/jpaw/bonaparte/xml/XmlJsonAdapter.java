package de.jpaw.bonaparte.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.util.FreezeTools;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class XmlJsonAdapter extends XmlAdapter<XmlJsonAdapter.JSON, Map<String, Object>> {

    @XmlType(name="JSON", namespace="http://www.jpaw.de/schema/bonaparte.xsd")
    public static class JSON {
        public List<KVP>     kvp;  // key / value pairs
    }
    @XmlType(name="KVP", namespace="http://www.jpaw.de/schema/bonaparte.xsd")
    public static class KVP {
        public String        key;
        public Double        num;
        public List<Double>  nums;
        public String        value;
        public List<String>  values;
        public Boolean       bool;
        public List<Boolean> bools;
        public JSON          obj;
        public List<JSON>    objs;
        public Object        any;
        public List<Object>  anys;
    }

    private static Object firstNonNull(Object ... objs) {
        for (Object o : objs)
            if (o != null)
                return o;
        return null;
    }

    // utility method to unmarshal a nested list of JSON objects
    protected List<Map<String, Object>> unmarshal(List<JSON> entries) throws Exception {
        if (entries == null)
            return null;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(entries.size());
        for (JSON e : entries)
            result.add(unmarshal(e));
        return result;
    }

    @Override
    public Map<String, Object> unmarshal(JSON entries) throws Exception {
        if (entries == null || entries.kvp == null)
            return null;
        final Map<String, Object> map = new HashMap<String, Object>(FreezeTools.getInitialHashMapCapacity(entries.kvp.size()));
        for(KVP kvp : entries.kvp) {
            map.put(kvp.key, firstNonNull(kvp.num, kvp.nums, kvp.value, kvp.values, kvp.bool, kvp.bools, unmarshal(kvp.obj), unmarshal(kvp.objs), kvp.any, kvp.anys));
        }
        return map;
    }

    // utility method to marshal a nested list of JSON objects
    protected List<JSON> marshal(List<Map<String, Object>> maps) throws Exception {
        if (maps == null)
            return null;
        List<JSON> result = new ArrayList<JSON>(maps.size());
        for (Map<String, Object> e : maps)
            result.add(marshal(e));
        return result;
    }

    @Override
    public JSON marshal(Map<String, Object> map) throws Exception {
        if (map == null)
            return null;
        List<KVP> xml = new ArrayList<KVP>(map.size());
        for(Map.Entry<String, Object> mapEntry : map.entrySet()) {
            KVP entry = new KVP();
            entry.key = mapEntry.getKey();
            Object v  = mapEntry.getValue();
            if (v != null) {
                if (v instanceof List) {
                    List l = (List)v;
                    if (l.size() > 0) {
                        Object w = l.get(0);
                        if (w instanceof Number)
                            entry.nums = convertToDoubleList(l);
                        else if (w instanceof String)
                            entry.values = l;
                        else if (w instanceof Boolean)
                            entry.bools = l;
                        else if (w instanceof Map)
                            entry.objs = marshal((List<Map<String, Object>>)l);
                        else
                            entry.any = v;  // fallback
                    } // no action if empty list - cannot determine type, leave null
                } else {
                    if (v instanceof Double)
                        entry.num = (Double)v;  // explicit check for Double to avoid unbox/box
                    else if (v instanceof Number)
                        entry.num = ((Number)v).doubleValue();
                    else if (v instanceof String)
                        entry.value = (String)v;
                    else if (v instanceof Boolean)
                        entry.bool = (Boolean)v;
                    else if (v instanceof Map)
                        entry.obj = marshal((Map<String, Object>)v);
                    else
                        entry.any = v;  // fallback
                }
            }
            xml.add(entry);
        }
        JSON result = new JSON();
        result.kvp = xml;
        return result;
    }

    protected List<Double> convertToDoubleList(List src) {
        final List<Double> dst = new ArrayList<>(src.size());
        for (Object o: src) {
            if (o == null) {
                // should not happen, really!
                dst.add(null);
            } else if (o instanceof Double) {
                dst.add((Double)o);
            } else if (o instanceof Number) {
                // convert int, long, BigDecimal to Double, otherwise JAXB will squeeze in some xsi:int etc... which violates the xsd
                dst.add(((Number)o).doubleValue());
            } else {
                // last resort to obtain some numeric value...
                dst.add(Double.valueOf(o.toString()));
            }
        }
        return dst;
    }
}
