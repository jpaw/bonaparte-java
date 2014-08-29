package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.MicroUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Micros;

public class MicroUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, MicroUnits obj, MessageComposer<E> w) throws E {
        w.addField(Micros.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> MicroUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Micros.meta$$mantissa);
        return mantissa == null ? null : MicroUnits.of(mantissa.longValue());
    }
}
