package de.jpaw.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigMillis;

public class BigMilliUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, MilliUnits obj, MessageComposer<E> w) throws E {
        w.addField(BigMillis.meta$$mantissa, BigDecimal.valueOf(obj.getMantissa(), obj.getScale()));
    }
    
    public static <E extends Exception> MilliUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        BigDecimal num = p.readBigDecimal(BigMillis.meta$$mantissa);
        return num == null ? null : MilliUnits.of(num.unscaledValue().longValue());
    }
}
