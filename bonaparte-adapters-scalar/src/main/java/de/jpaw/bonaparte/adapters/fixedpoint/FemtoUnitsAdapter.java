package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.FemtoUnits;

public class FemtoUnitsAdapter {

    public static <E extends Exception> Long marshal(FemtoUnits obj) throws E {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> FemtoUnits unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : FemtoUnits.of(mantissa.longValue());
    }
}
