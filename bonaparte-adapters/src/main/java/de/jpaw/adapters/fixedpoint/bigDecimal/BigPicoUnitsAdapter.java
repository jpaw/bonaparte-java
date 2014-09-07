package de.jpaw.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.PicoUnits;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigPicos;

public class BigPicoUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, PicoUnits obj, MessageComposer<E> w) throws E {
        w.addField(BigPicos.meta$$mantissa, BigDecimal.valueOf(obj.getMantissa(), obj.getScale()));
    }
    
    public static <E extends Exception> PicoUnits unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        BigDecimal num = p.readBigDecimal(BigPicos.meta$$mantissa);
        return num == null ? null : PicoUnits.of(num.unscaledValue().longValue());
    }
}
