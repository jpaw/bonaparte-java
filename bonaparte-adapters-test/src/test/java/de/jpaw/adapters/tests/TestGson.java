package de.jpaw.adapters.tests;

import org.testng.annotations.Test;

import com.google.gson.JsonObject;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.pojos.adapters.tests.DataWithJsonObj;

public class TestGson {

    @Test
    public void testAdapterGsonObj() throws Exception {
        JsonObject zz = new JsonObject();
        zz.addProperty("count", 42);
        zz.addProperty("canIuse", true);
        zz.addProperty("greeting", "hello, world");

        DataWithJsonObj test1 = new DataWithJsonObj("Standard data", zz);
        String serialized = JsonComposer.toJsonString(test1);

        System.out.println("Object is " + serialized);

        // output is Object is {"text":"Standard data","obj":"{\"count\":42,\"canIuse\":true,\"greeting\":\"hello, world\"}"}
        // which is OK, but the escaped quotes indicate that the integration is not seamless.
    }
}
