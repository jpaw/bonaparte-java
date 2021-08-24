package de.jpaw.bonaparte.servlet;

import javax.servlet.http.HttpServletRequest;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.core.StringProviderParser;

/** Parser which parses parameters from headers of a HttpRequest. */
public class HttpRequestParameterParser extends StringProviderParser {

    public static StringProviderParser.StringGetter createGetter(final HttpServletRequest request) {
        return new StringProviderParser.StringGetter() {
            @Override
            public String get(String name) {
                return request.getParameter(name);
            }
        };
    }

    public HttpRequestParameterParser(final HttpServletRequest request) {
        super(createGetter(request));
    }

    /** unmarshals request parameters into a preallocated object.
     * @throws MessageParserException */
    public static void unmarshal(HttpServletRequest request, BonaPortable obj) throws MessageParserException {
        StringProviderParser.unmarshal(obj, createGetter(request));
    }
}
