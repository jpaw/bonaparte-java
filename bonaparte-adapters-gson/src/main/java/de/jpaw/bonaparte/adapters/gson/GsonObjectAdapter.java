package de.jpaw.bonaparte.adapters.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.jpaw.bonaparte.core.ExceptionConverter;

public class GsonObjectAdapter {

    public static String marshal(JsonObject obj) {
        return obj.toString();
    }

    public static <E extends Exception> JsonObject unmarshal(String str, ExceptionConverter<E> p) throws E {
        if (str == null)
            return null;
        try {
            JsonElement elem = new JsonParser().parse(str);
            if (elem instanceof JsonObject)
                return (JsonObject)elem;
            throw new Exception("Parsed JSON is not an object");
        } catch (Exception e) {
            throw p.customExceptionConverter("cannot parse JSON object", e);
        }
    }
}
