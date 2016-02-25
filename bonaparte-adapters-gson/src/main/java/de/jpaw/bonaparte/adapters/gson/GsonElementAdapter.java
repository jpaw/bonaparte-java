package de.jpaw.bonaparte.adapters.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.jpaw.bonaparte.core.ExceptionConverter;

public class GsonElementAdapter {

    public static String marshal(JsonElement obj) {
        return obj.toString();
    }

    public static <E extends Exception> JsonElement unmarshal(String str, ExceptionConverter<E> p) throws E {
        if (str == null)
            return null;
        try {
            return new JsonParser().parse(str);
        } catch (Exception e) {
            throw p.customExceptionConverter("cannot parse JSON Element", e);
        }
    }
}
