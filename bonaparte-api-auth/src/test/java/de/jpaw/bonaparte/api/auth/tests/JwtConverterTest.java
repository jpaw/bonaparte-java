package de.jpaw.bonaparte.api.auth.tests;

import java.util.Map;
import java.util.UUID;

import java.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.bonaparte.api.auth.JwtConverter;
import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.pojos.api.OperationType;
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo;
import de.jpaw.bonaparte.pojos.api.auth.JwtPayload;
import de.jpaw.bonaparte.pojos.api.auth.Permissionset;
import de.jpaw.bonaparte.pojos.api.auth.UserLogLevelType;
import de.jpaw.bonaparte.util.ToStringHelper;

public class JwtConverterTest {

    private JwtInfo createSampleInfo(Instant now, UUID sessionId) {
        // test one field per data type at least
        JwtInfo info = new JwtInfo();
        info.setUserId("John");
        info.setUserRef(4711L);
        info.setIssuedAt(now);
        info.setLogLevel(UserLogLevelType.REQUESTS);
        info.setPermissionsMax(Permissionset.ofTokens(OperationType.EXECUTE, OperationType.SEARCH));
        info.setResource("B.test");
        info.setResourceIsWildcard(Boolean.TRUE);
        info.setSessionId(sessionId);
        return info;
    }

    @Test
    public void testInfoToMap() throws Exception {
        Instant now = JwtConverter.lastFullSecond();
        UUID sessionId = UUID.randomUUID();

        // test one field per data type at least
        JwtInfo info = createSampleInfo(now, sessionId);

        Map<String, Object> jsonMap = JwtConverter.asMap(info);
        Assertions.assertEquals(jsonMap.size(), 8);
        Assertions.assertEquals(jsonMap.get("sub"), "John");
        Assertions.assertEquals(jsonMap.get("iat"), now);
        Assertions.assertEquals(jsonMap.get("u"), Long.valueOf(4711L));
        Assertions.assertEquals(jsonMap.get("l"),  UserLogLevelType.REQUESTS.ordinal());
        Assertions.assertEquals(jsonMap.get("pu"), Permissionset.ofTokens(OperationType.EXECUTE, OperationType.SEARCH).getBitmap());
        Assertions.assertEquals(jsonMap.get("w"), Boolean.TRUE);
        Assertions.assertEquals(jsonMap.get("p"), "B.test");
        Assertions.assertEquals(jsonMap.get("o"), sessionId);

        // test the conversion back to the JwtInfo
        JwtPayload payload = JwtConverter.parsePayload(jsonMap);
        JwtInfo info2 = JwtConverter.parseJwtInfo(payload);
        System.out.println(ToStringHelper.toStringML(payload));
        System.out.println(ToStringHelper.toStringML(info2));
        Assertions.assertEquals(info2, info);
    }

    @Test
    public void testInfoJson() {
        Instant now = JwtConverter.lastFullSecond();
        UUID sessionId = UUID.randomUUID();

        // test one field per data type at least
        JwtInfo info = createSampleInfo(now, sessionId);
        String json = BonaparteJsonEscaper.asJson(JwtConverter.asMap(info));
        System.out.println(json);
    }
}
