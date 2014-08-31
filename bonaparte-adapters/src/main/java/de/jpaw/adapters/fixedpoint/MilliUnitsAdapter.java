package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Millis;

public class MilliUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, MilliUnits obj, MessageComposer<E> w) throws E {
        w.addField(Millis.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> MilliUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Millis.meta$$mantissa);
        return mantissa == null ? null : MilliUnits.of(mantissa.longValue());
    }
}
