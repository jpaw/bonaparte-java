package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.s;

public class UnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Units obj, MessageComposer<E> w) throws E {
        w.addField(s.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> Units unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(s.meta$$mantissa);
        return mantissa == null ? null : Units.of(mantissa.longValue());
    }
}
