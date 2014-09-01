package de.jpaw.adapters.fixedpoint;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.Tenths;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class TenthsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Tenths obj, MessageComposer<E> w) throws E {
        w.addField(Tenths.meta$$mantissa, obj.getMantissa());
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Tenths unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        Long mantissa = p.readLong(Tenths.meta$$mantissa);
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Tenths.of(mantissa.longValue());
    }
}
