package de.jpaw.bonaparte.api;

import java.util.Map;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.api.auth.Alg;
import de.jpaw.bonaparte.pojos.api.auth.Jwt;
import de.jpaw.bonaparte.pojos.api.auth.Payload;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;

/** Conversions between JSON strings and Bonaparte classes for parts of the JWT. */
public class JwtConverter {

    public static Map<String,Object> algAsMap(String json) throws JsonException {
        return new JsonParser(json, true).parseObject();
    }
    
    public static Alg parseAlg(String json) throws ApplicationException {
        final Alg alg = (Alg) MapParser.asBonaPortable(new JsonParser(json, true).parseObject(), Jwt.meta$$alg);
        alg.freeze();
        return alg;
    }
    
    public static Payload parsePayload(String json) throws ApplicationException {
        final Payload payload = (Payload) MapParser.asBonaPortable(new JsonParser(json, true).parseObject(), Jwt.meta$$payload);
        payload.freeze();
        return payload;
    }
    
    public static String toJson(Alg alg) {
        return JsonComposer.toJsonString(alg);
    }
    
    public static String toJson(Payload payload) {
        return JsonComposer.toJsonString(payload);
    }
}
