package de.jpaw.bonaparte.api;

import java.util.Map;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.api.auth.JwtAlg;
import de.jpaw.bonaparte.pojos.api.auth.Jwt;
import de.jpaw.bonaparte.pojos.api.auth.JwtPayload;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;

/** Conversions between JSON strings and Bonaparte classes for parts of the JWT. */
public class JwtConverter {

    public static Map<String,Object> algAsMap(String json) throws JsonException {
        return new JsonParser(json, true).parseObject();
    }
    
    public static JwtAlg parseAlg(String json) throws ApplicationException {
        final JwtAlg alg = (JwtAlg) MapParser.asBonaPortable(new JsonParser(json, true).parseObject(), Jwt.meta$$alg);
        alg.freeze();
        return alg;
    }
    
    public static JwtPayload parsePayload(String json) throws ApplicationException {
        final JwtPayload payload = (JwtPayload) MapParser.asBonaPortable(new JsonParser(json, true).parseObject(), Jwt.meta$$payload);
        payload.freeze();
        return payload;
    }
    
    public static String toJson(JwtAlg alg) {
        return JsonComposer.toJsonString(alg);
    }
    
    public static String toJson(JwtPayload payload) {
        return JsonComposer.toJsonString(payload);
    }
}
