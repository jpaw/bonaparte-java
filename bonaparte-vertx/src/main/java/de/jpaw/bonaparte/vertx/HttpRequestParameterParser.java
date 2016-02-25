package de.jpaw.bonaparte.vertx;

import org.vertx.java.core.MultiMap;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringProviderParser;

/** A parser which takes data from a provided vert.x MultiMap. This is a simple application of the generic StringProviderParser. */
public class HttpRequestParameterParser extends StringProviderParser {

    public static StringProviderParser.StringGetter createGetter(final MultiMap requestParameters) {
        return new StringProviderParser.StringGetter() {
            @Override
            public String get(String name) {
                return requestParameters.get(name);
            }
        };
    }

    public HttpRequestParameterParser(final MultiMap request) {
        super(createGetter(request));
    }

    /** unmarshals request parameters into a preallocated object.
     * @throws MessageParserException */
    public static void unmarshal(MultiMap request, BonaPortable obj) throws MessageParserException {
        StringProviderParser.unmarshal(obj, createGetter(request));
    }
}
