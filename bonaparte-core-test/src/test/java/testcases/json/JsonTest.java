package testcases.json;

import java.time.Instant;
import java.time.LocalDate;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.cedarsoftware.util.io.JsonWriter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.JsonComposer;
import de.jpaw.bonaparte.pojos.jsonTest.TestObj;

public class JsonTest {

    private TestObj getData() {
        return new TestObj(42, 42424242424242L, LocalDate.of(2014, 12, 31), Instant.now(), "Hello, world\n", true, null);
    }
    private TestObj[] getArrayData() {
        TestObj x = new TestObj(42, 42424242424242L, LocalDate.of(2014, 12, 31), Instant.now(), "Hello, world\n", true, null);
        TestObj [] array = new TestObj[3];
        array[0] = x;
        array[1] = x;
        array[2] = x;
        return array;
    }

    @Test
    public void testEscaping() throws Exception {
        StringBuilder buff = new StringBuilder(4000);
        JsonComposer bjc = new JsonComposer(buff);
        bjc.addField(TestObj.meta$$myText, "E\nS\bC");
        Assert.assertEquals(buff.toString(), "\"myText\":\"E\\nS\\bC\"");
    }



    @Test
    public void runBona() throws Exception {
        System.out.println("Bonaparte produces " + JsonComposer.toJsonString(getData()));
        // arrays not possible with Bonaparte. Must do multiple invocations
    }

    @Test
    public void runGson() throws Exception {
        Gson gson = new Gson();
        System.out.println("Gson produces " + gson.toJson(getData()));
        System.out.println("Gson array produces " + gson.toJson(getArrayData()));
    }

    @Test
    public void runJsonIO() throws Exception {
        System.out.println("Json-io produces " + JsonWriter.objectToJson(getData()));
        System.out.println("Json-io array produces " + JsonWriter.objectToJson(getArrayData()));
    }



    // Jackson needs a bit more to work...
    @JsonFilter("regexFilter")
    private static class RegexFilterMixIn {}

    private static class RegexBeanPropertyFilter extends SimpleBeanPropertyFilter {
        private String pattern;

        public RegexBeanPropertyFilter(final String pattern) {
            this.pattern = pattern;
        }

        @Override
        protected boolean include(final PropertyWriter writer) {
            return !writer.getName().matches(pattern);
        }
    }

    private static class BonaparteModule extends SimpleModule {
        private static final long serialVersionUID = 6347925137677709885L;

        public BonaparteModule() {
            super("BonaparteModule");
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(BonaPortable.class, RegexFilterMixIn.class);
        }
    }

    @Test
    public void runJackson() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new BonaparteModule());
        mapper.setSerializationInclusion(Include.NON_NULL);

        // mapped "regexFilter" ID to actual filter for filtering
        FilterProvider filters = new SimpleFilterProvider().addFilter("regexFilter", new RegexBeanPropertyFilter("\\$.*"));
        System.out.println("Jackson produces " + mapper.writer(filters).writeValueAsString(getData()));
    }

    // Jackson after bonaparte 3.4.0: no filter required (but still Joda module)
    @Test
    public void runJackson2() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        System.out.println("Jackson2 produces " + mapper.writer().writeValueAsString(getData()));
    }
}
