package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.NanoUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Nanos;

public class NanoUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, NanoUnits obj, MessageComposer<E> w) throws E {
        w.addField(Nanos.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> NanoUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Nanos.meta$$mantissa);
        return mantissa == null ? null : NanoUnits.of(mantissa.longValue());
    }
}
