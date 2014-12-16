package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.MicroUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigMicros;

public class BigMicroUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, MicroUnits obj, MessageComposer<E> w) throws E {
        w.addField(BigMicros.meta$$mantissa, BigDecimal.valueOf(obj.getMantissa(), obj.getScale()));
    }
    
    public static <E extends Exception> MicroUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        BigDecimal num = p.readBigDecimal(BigMicros.meta$$mantissa);
        return num == null ? null : MicroUnits.of(num.unscaledValue().longValue());
    }
}
