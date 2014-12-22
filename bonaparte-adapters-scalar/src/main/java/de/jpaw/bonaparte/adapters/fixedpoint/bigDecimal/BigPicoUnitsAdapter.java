package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.PicoUnits;

public class BigPicoUnitsAdapter {

    public static <E extends Exception> BigDecimal marshal(PicoUnits obj) throws E {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }
    
    public static <E extends Exception> PicoUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : PicoUnits.of(num.unscaledValue().longValue());
    }
}
