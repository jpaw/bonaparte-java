package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.MilliUnits;

public class MilliUnitsAdapter {

    public static Long marshal(MilliUnits obj) {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> MilliUnits unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : MilliUnits.of(mantissa.longValue());
    }
}
