package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Units;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class UnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Units obj, MessageComposer<E> w) throws E {
        w.addField(Units.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Units unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Units.meta$$mantissa);
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Units.of(mantissa.longValue());
    }
}
