package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Hundreds;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class HundredsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Hundreds obj, MessageComposer<E> w) throws E {
        w.addField(Hundreds.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Hundreds unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Hundreds.meta$$mantissa);
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Hundreds.of(mantissa.longValue());
    }
}
