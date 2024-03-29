package de.jpaw.bonaparte.api.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.time.Instant;

import de.jpaw.bonaparte.core.DataAndMeta;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.ListMetaComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.core.MimeTypes;
import de.jpaw.bonaparte.pojos.api.auth.Jwt;
import de.jpaw.bonaparte.pojos.api.auth.JwtAlg;
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo;
import de.jpaw.bonaparte.pojos.api.auth.JwtPayload;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;
import de.jpaw.util.ApplicationException;

/** Conversions between JSON strings and Bonaparte classes for parts of the JWT. */
public class JwtConverter {
    private static final String PAYLOAD_PQON = JwtPayload.BClass.INSTANCE.getPqon();

    /** returns the current time, rounded down to the previous full second. */
    public static Instant lastFullSecond() {
        long now = System.currentTimeMillis();
        return Instant.ofEpochSecond(now - now % 1000L);
    }

    public static Map<String,Object> asMap(String json) throws JsonException {
        return new JsonParser(json, true).parseObject();
    }

    public static JwtAlg parseAlg(String json) throws ApplicationException {
        final JwtAlg alg = (JwtAlg) MapParser.asBonaPortable(new JsonParser(json, true).parseObject(), Jwt.meta$$alg);
        alg.freeze();
        return alg;
    }

    public static JwtPayload parsePayload(Map<String,Object> map) throws ApplicationException {
        final JwtPayload payload = (JwtPayload) MapParser.asBonaPortable(map, Jwt.meta$$payload);
        payload.freeze();
        return payload;
    }

    public static JwtPayload parsePayload(String json) throws ApplicationException {
        final Map<String,Object> map = new JsonParser(json, true).parseObject();
        // if no object type has been specified, provide the default type to avoid a warning, unless fqon or pqon is specified
        Object pqon1 = map.get(MimeTypes.JSON_FIELD_PQON);
        Object pqon2 = map.get(MimeTypes.JSON_FIELD_FQON);
        if (!((pqon1 != null && pqon1 instanceof String) || (pqon2 != null && pqon2 instanceof String)))
            map.put(MimeTypes.JSON_FIELD_PQON, PAYLOAD_PQON);
        return parsePayload(map);
    }

    public static String toJson(JwtAlg alg) {
        return JsonComposer.toJsonString(alg);
    }

    public static String toJson(JwtPayload payload) {
        return JsonComposer.toJsonString(payload);
    }


    public static JwtInfo parseJwtInfo(String json) throws ApplicationException {
        return parseJwtInfo(parsePayload(json));      // use the intermediate object to avoid the hassle of handwritten type checks
    }

    public static JwtInfo parseJwtInfo(JwtPayload payload) throws ApplicationException {
        final JwtInfo info = new JwtInfo();
        info.setIssuer              (payload.getIss());
        info.setUserId              (payload.getSub());
        info.setAudience            (payload.getAud());
        info.setExpiresAt           (payload.getExp());
        info.setNotBefore           (payload.getNbf());
        info.setIssuedAt            (payload.getIat());
        info.setJsonTokenIdentifier (payload.getJti());

        info.setName                (payload.getName());
        info.setLocale              (payload.getLocale());
        info.setZoneinfo            (payload.getZoneinfo());

        info.setTenantId            (payload.getI());
        info.setTenantRef           (payload.getT());
        info.setSessionRef          (payload.getS());
        info.setUserRef             (payload.getU());
        info.setRoleRef             (payload.getR());
        info.setSessionId           (payload.getO());
        info.setQuota               (payload.getQ());
        info.setLogLevel            (payload.getL());
        info.setLogLevelErrors      (payload.getE());
        info.setResource            (payload.getP());
        info.setResourceIsWildcard  (payload.getW());
        info.setPermissionsMin      (payload.getPl());
        info.setPermissionsMax      (payload.getPu());
        info.setZ                   (payload.getZ());
        info.freeze();
        return info;
    }

    // converter from JwtInfo into a JWT Map uses a composer
    private static class MapWithTagKeysComposer extends ListMetaComposer {
        private final Map<String,Object> target;
        private final Map<String, String> properties;

        private MapWithTagKeysComposer(Map<String,Object> target, Map<String, String> properties) {
            super(new ArrayList<DataAndMeta>(0), false, false, false, true);
            this.target = target;
            this.properties = properties;
        }

        @Override
        protected void add(FieldDefinition di, Object o) {
            if (o != null) {
                String key = properties.get(di.getName() + ".tag");
                if (key == null) {
                    // throw new RuntimeException("No JWT tag defined for JwtInfo field " + di.getName());
                } else {
                    target.put(key, o);
                }
            }
        }
    }

    /** Returns the JwtInfo as a Payload map. */
    public static Map<String,Object> asMap(JwtInfo info) {
        final Map<String, Object> jsonMap = new HashMap<String, Object>(16);
        new MapWithTagKeysComposer(jsonMap, JwtInfo.class$MetaData().getProperties()).writeObject(info);
        return jsonMap;
    }
}
