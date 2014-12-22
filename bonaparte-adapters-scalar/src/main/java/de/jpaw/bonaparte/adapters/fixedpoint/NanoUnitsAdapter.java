package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.NanoUnits;

public class NanoUnitsAdapter {

    public static Long marshal(NanoUnits obj) {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> NanoUnits unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : NanoUnits.of(mantissa.longValue());
    }
}
