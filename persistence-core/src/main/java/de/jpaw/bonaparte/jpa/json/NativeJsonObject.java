package de.jpaw.bonaparte.jpa.json;

import java.io.Serializable;
import java.util.Map;

/** Wrapper class to store general items which should be converted into database specific native JSON objects.
 * Provided as a hook for the converters.
 *
 * @author mbi
 *
 */
public final class NativeJsonObject extends AbstractJsonObject implements Serializable {
    private static final long serialVersionUID = 6234763103220203042L;

    public NativeJsonObject(Map<String, Object> data) {
        super(data);
    }
}
