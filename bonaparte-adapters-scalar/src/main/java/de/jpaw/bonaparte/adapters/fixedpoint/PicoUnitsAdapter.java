package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.PicoUnits;

public class PicoUnitsAdapter {

    public static Long marshal(PicoUnits obj) {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> PicoUnits unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : PicoUnits.of(mantissa.longValue());
    }
}
