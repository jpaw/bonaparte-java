package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.NanoUnits;

public class BigNanoUnitsAdapter {

    public static <E extends Exception> BigDecimal marshal(NanoUnits obj) throws E {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }
    
    public static <E extends Exception> NanoUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : NanoUnits.of(num.unscaledValue().longValue());
    }
}
