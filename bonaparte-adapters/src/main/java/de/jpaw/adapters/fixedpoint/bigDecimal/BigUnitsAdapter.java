package de.jpaw.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.types.Units;
import de.jpaw.bonaparte.pojos.adapters.fixedpointToBigDecimal.BigFemtos;

public class BigUnitsAdapter {

    public static <E extends Exception> void marshal(ObjectReference di, Units obj, MessageComposer<E> w) throws E {
        w.addField(BigFemtos.meta$$mantissa, BigDecimal.valueOf(obj.getMantissa(), obj.getScale()));
    }
    
    public static <E extends Exception> Units unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        BigDecimal num = p.readBigDecimal(BigFemtos.meta$$mantissa);
        return num == null ? null : Units.of(num.unscaledValue().longValue());
    }
}
