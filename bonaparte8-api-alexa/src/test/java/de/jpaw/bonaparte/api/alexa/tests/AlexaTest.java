package de.jpaw.bonaparte.api.alexa.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.core.MapParser;
import de.jpaw.bonaparte.pojos.api.alexa.AlexaApplicationIn;
import de.jpaw.bonaparte.pojos.api.alexa.AlexaOutputSpeech;
import de.jpaw.bonaparte.pojos.api.alexa.AlexaSessionIn;
import de.jpaw.bonaparte.pojos.api.alexa.SpeechType;
import de.jpaw.bonaparte.util.ToStringHelper;
import de.jpaw.json.JsonParser;

public class AlexaTest {
    public static final String JSON1 = "{\"type\":\"PlainText\",\"text\":\"Hello world\"}\n";
    public static final String JSON2 = "{\"new\":true,\"application\":{\"applicationId\":\"myApp\"},\"attributes\":{}}\n";

    // validates enums are output as tokens
    @Test
    public void serialize() throws Exception {
        AlexaOutputSpeech os = new AlexaOutputSpeech();
        os.setType(SpeechType.PLAIN_TEXT);
        os.setText("Hello world");

        String out = JsonComposer.toJsonStringNoPQON(os);

        System.out.println(out);
        Assert.assertEquals(JSON1, out);
    }

    // validates metaName is used for output
    @Test
    public void serialize2() throws Exception {
        AlexaSessionIn si = new AlexaSessionIn();
        si.setIsNew(true);
        si.setApplication(new AlexaApplicationIn("myApp"));
        si.setAttributes(new HashMap<String, Object>());

        String out = JsonComposer.toJsonStringNoPQON(si);

        System.out.println(out);
        Assert.assertEquals(JSON2, out);
    }

    @Test
    public void deserialize() throws Exception {
        Map<String, Object> map = new JsonParser(JSON1, false).parseObject();
        AlexaOutputSpeech os = new AlexaOutputSpeech();
        MapParser.populateFrom(os, map);

        System.out.println(ToStringHelper.toStringML(os));
        Assert.assertTrue(SpeechType.PLAIN_TEXT == os.getType());
    }

    @Test
    public void deserialize2() throws Exception {
        // BonaPortableFactory.addToPackagePrefixMap("alexa.api", "addToPackagePrefixMap");
        Map<String, Object> map = new JsonParser(JSON2, false).parseObject();
        AlexaSessionIn si = new AlexaSessionIn();
        MapParser.populateFrom(si, map);
        System.out.println(ToStringHelper.toStringML(si));
        Assert.assertTrue(si.getIsNew());
    }
}
