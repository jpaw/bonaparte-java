package de.jpaw.bonaparte.jpa.json;

import java.io.Serializable;
import java.util.List;

/** Wrapper class to store general items which should be converted into database specific native JSON objects.
 * Provided as a hook for the converters.
 *
 * @author mbi
 *
 */
public abstract class AbstractJsonArray implements Serializable {
    private static final long serialVersionUID = 6234763103220203020L;

    protected AbstractJsonArray(List<Object> data) {
        this.data = data;
    }

    private List<Object> data;

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    // standard Java stuff: toString(), hashCode(), equals()

    @Override
    public String toString() {
        return data == null ? "(null)" : data.toString();
    }

    @Override
    public int hashCode() {
        return data == null ? 0 : data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractJsonArray other = (AbstractJsonArray) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }
}
