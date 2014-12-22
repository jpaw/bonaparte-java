package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.UnitsAd;

public class UnitsAdapter {

    public static Long marshal(UnitsAd obj) {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Units unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Units.of(mantissa.longValue());
    }
}
