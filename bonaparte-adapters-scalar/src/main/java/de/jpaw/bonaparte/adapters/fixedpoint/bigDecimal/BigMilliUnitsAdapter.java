package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.MilliUnits;

public class BigMilliUnitsAdapter {

    public static BigDecimal marshal(MilliUnits obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }
    
    public static <E extends Exception> MilliUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : MilliUnits.of(num.unscaledValue().longValue());
    }
}
