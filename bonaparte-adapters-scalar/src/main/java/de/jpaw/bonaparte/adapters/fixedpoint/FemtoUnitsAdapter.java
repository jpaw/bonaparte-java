package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.FemtoUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Femtos;

public class FemtoUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, FemtoUnits obj, MessageComposer<E> w) throws E {
        w.addField(Femtos.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> FemtoUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Femtos.meta$$mantissa);
        return mantissa == null ? null : FemtoUnits.of(mantissa.longValue());
    }
}
