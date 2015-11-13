package de.jpaw.bonaparte.api.auth.tests;

import java.util.Map;

import org.joda.time.Instant;
import org.testng.Assert;
import org.testng.annotations.Test;

import de.jpaw.bonaparte.api.auth.JwtConverter;
import de.jpaw.bonaparte.pojos.api.OperationType;
import de.jpaw.bonaparte.pojos.api.auth.JwtInfo;
import de.jpaw.bonaparte.pojos.api.auth.Permissionset;
import de.jpaw.bonaparte.pojos.api.auth.UserLogLevelType;

public class JwtConverterTest {

    @Test
    public void testInfoToMap() throws Exception {
        Instant now = new Instant();
        JwtInfo info = new JwtInfo();
        info.setUserId("John");
        info.setIssuedAt(now);
        info.setLogLevel(UserLogLevelType.REQUESTS);
        info.setPermissionsMax(Permissionset.of(OperationType.EXECUTE, OperationType.SEARCH));
        
        Map<String, Object> jsonMap = JwtConverter.asMap(info);
        Assert.assertEquals(jsonMap.size(), 4);
        Assert.assertEquals(jsonMap.get("sub"), "John");
        Assert.assertEquals(jsonMap.get("iat"), now);
        Assert.assertEquals(jsonMap.get("l"),   UserLogLevelType.REQUESTS.ordinal());
        Assert.assertEquals(jsonMap.get("pu"),  Permissionset.of(OperationType.EXECUTE, OperationType.SEARCH).getBitmap());
    }
}
