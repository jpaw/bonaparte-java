package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.Units;

public class UnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Units obj, MessageComposer<E> w) throws E {
        w.addField(de.jpaw.bonaparte.pojos.adapters.fixedpoint.Units.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> Units unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(de.jpaw.bonaparte.pojos.adapters.fixedpoint.Units.meta$$mantissa);
        return mantissa == null ? null : Units.of(mantissa.longValue());
    }
}
