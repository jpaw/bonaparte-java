package de.jpaw.bonaparte8.vertx3;


import io.vertx.core.MultiMap;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringProviderParser;

/** A parser which takes data from a provided vert.x MultiMap. This is a simple application of the generic StringProviderParser. */
public class HttpRequestParameterParser extends StringProviderParser {

    public HttpRequestParameterParser(final MultiMap request) {
        super((name) -> { return request.get(name); });
    }

    /** unmarshals request parameters into a preallocated object.
     * @throws MessageParserException */
    public static void unmarshal(final MultiMap request, BonaPortable obj) throws MessageParserException {
        StringProviderParser.unmarshal(obj, (name) -> { return request.get(name); });
    }
}
