package de.jpaw.bonaparte.jpa.json;

import java.io.Serializable;
import java.util.List;

/** Wrapper class to store general items which should be converted into database specific native JSON objects.
 * Provided as a hook for the converters.
 *
 * @author mbi
 *
 */
public final class NativeJsonArray extends AbstractJsonArray implements Serializable {
    private static final long serialVersionUID = 6234763103220203040L;

    public NativeJsonArray(List<Object> data) {
        super(data);
    }
}
