package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.NanoUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigNanos;

public class BigNanoUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, NanoUnits obj, MessageComposer<E> w) throws E {
        w.addField(BigNanos.meta$$mantissa, BigDecimal.valueOf(obj.getMantissa(), obj.getScale()));
    }
    
    public static <E extends Exception> NanoUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        BigDecimal num = p.readBigDecimal(BigNanos.meta$$mantissa);
        return num == null ? null : NanoUnits.of(num.unscaledValue().longValue());
    }
}
