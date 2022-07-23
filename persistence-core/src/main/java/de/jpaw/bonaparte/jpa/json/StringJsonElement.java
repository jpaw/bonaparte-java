package de.jpaw.bonaparte.jpa.json;

import java.io.Serializable;

/** Wrapper class to store general items which should be converted into database specific native JSON objects.
 * Provided as a hook for the converters.
 *
 * @author mbi
 *
 */
public final class StringJsonElement extends AbstractJsonElement implements Serializable {
    private static final long serialVersionUID = 6234763103220203051L;

    public StringJsonElement(Object data) {
        super(data);
    }
}
