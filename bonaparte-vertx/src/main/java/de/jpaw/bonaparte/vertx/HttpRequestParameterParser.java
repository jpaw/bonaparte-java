package de.jpaw.bonaparte.vertx;

import org.vertx.java.core.MultiMap;

import de.jpaw.bonaparte.core.StringProviderParser;

/** A parser which takes data from a provided vert.x MultiMap. This is a simple application of the generic StringProviderParser. */
public class HttpRequestParameterParser extends StringProviderParser {

    public HttpRequestParameterParser(final MultiMap requestParameters) {
        super(new StringProviderParser.StringGetter() {
            @Override
            public String get(String name) {
                return requestParameters.get(name);
            }
        });
    }
}
