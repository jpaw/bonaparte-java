package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.PicoUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Picos;

public class PicoUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, PicoUnits obj, MessageComposer<E> w) throws E {
        w.addField(Picos.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> PicoUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Picos.meta$$mantissa);
        return mantissa == null ? null : PicoUnits.of(mantissa.longValue());
    }
}
