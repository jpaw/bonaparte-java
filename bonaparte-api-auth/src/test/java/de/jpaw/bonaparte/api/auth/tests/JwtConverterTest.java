package de.jpaw.bonaparte.api.auth.tests;

import java.util.Map;
import java.util.UUID;

import org.joda.time.Instant;
import org.testng.Assert;
import org.testng.annotations.Test;

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
        info.setPermissionsMax(Permissionset.of(OperationType.EXECUTE, OperationType.SEARCH));
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
        Assert.assertEquals(jsonMap.size(), 8);
        Assert.assertEquals(jsonMap.get("sub"), "John");
        Assert.assertEquals(jsonMap.get("iat"), now);
        Assert.assertEquals(jsonMap.get("u"), Long.valueOf(4711L));
        Assert.assertEquals(jsonMap.get("l"),  UserLogLevelType.REQUESTS.ordinal());
        Assert.assertEquals(jsonMap.get("pu"), Permissionset.of(OperationType.EXECUTE, OperationType.SEARCH).getBitmap());
        Assert.assertEquals(jsonMap.get("w"), Boolean.TRUE);
        Assert.assertEquals(jsonMap.get("p"), "B.test");
        Assert.assertEquals(jsonMap.get("o"), sessionId);

        // test the conversion back to the JwtInfo
        JwtPayload payload = JwtConverter.parsePayload(jsonMap);
        JwtInfo info2 = JwtConverter.parseJwtInfo(payload);
        System.out.println(ToStringHelper.toStringML(payload));
        System.out.println(ToStringHelper.toStringML(info2));
        Assert.assertEquals(info2, info);
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
