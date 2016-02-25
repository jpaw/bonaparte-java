package de.jpaw.bonaparte.servlet;

import javax.servlet.http.HttpServletRequest;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringProviderParser;

/** Parser which parses parameters from headers of a HttpRequest.
 * With Java 8, the whole class would not be required. */
public class HttpHeaderParameterParser extends StringProviderParser {

    public static StringProviderParser.StringGetter createGetter(final HttpServletRequest request) {
        return new StringProviderParser.StringGetter() {
            @Override
            public String get(String name) {
                return request.getHeader(name.replace('_',  '-'));
            }
        };
    }

    public HttpHeaderParameterParser(final HttpServletRequest request) {
        super(createGetter(request));
    }

    /** unmarshals header parameters into a preallocated object.
     * @throws MessageParserException */
    public static void unmarshal(HttpServletRequest request, BonaPortable obj) throws MessageParserException {
        StringProviderParser.unmarshal(obj, createGetter(request));
    }
}
