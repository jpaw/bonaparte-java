package de.jpaw.bonaparte.api.auth;

import java.util.Map;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.api.auth.JwtAlg;
import de.jpaw.bonaparte.pojos.api.auth.Jwt;
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo;
import de.jpaw.bonaparte.pojos.api.auth.JwtPayload;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;

/** Conversions between JSON strings and Bonaparte classes for parts of the JWT. */
public class JwtConverter {

    public static Map<String,Object> asMap(String json) throws JsonException {
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
    
    
    public static JwtInfo parseJwtInfo(String json) throws ApplicationException {
        final JwtPayload payload = parsePayload(json);      // use the intermediate object to avoid the hassle of handwritten type checks
        final JwtInfo info = new JwtInfo();
        info.setIssuer              (payload.getIss());
        info.setUserId              (payload.getSub());
        info.setAudience            (payload.getAud());
        info.setExpiresAt           (payload.getExp());
        info.setNotBefore           (payload.getNbf());
        info.setIssuedAt            (payload.getIat());
        info.setJsonTokenIdentifier(payload.getJti());

        info.setName                (payload.getName());
        info.setLocale              (payload.getLocale());
        info.setZoneinfo            (payload.getZoneinfo());
        
        info.setTenantId            (payload.getI());
        info.setTenantRef           (payload.getT());
        info.setSessionRef          (payload.getS());
        info.setUserRef             (payload.getU());
        info.setRoleRef             (payload.getR());
        info.setLogLevel            (payload.getL());
        info.setLogLevelErrors      (payload.getE());
        info.setResource            (payload.getP());
        info.setPermissionsMin      (payload.getPl());
        info.setPermissionsMax      (payload.getPu());
        info.freeze();
        return info;
    }
}
