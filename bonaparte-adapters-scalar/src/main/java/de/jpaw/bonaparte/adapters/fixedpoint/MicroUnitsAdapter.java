package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.MicroUnits;

public class MicroUnitsAdapter {

    public static <E extends Exception> Long marshal(MicroUnits obj) throws E {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> MicroUnits unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : MicroUnits.of(mantissa.longValue());
    }
}
