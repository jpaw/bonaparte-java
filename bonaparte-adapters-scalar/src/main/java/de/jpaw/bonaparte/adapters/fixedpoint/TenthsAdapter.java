package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.TenthsAd;

public class TenthsAdapter {

    public static <E extends Exception> Long marshal(TenthsAd obj) throws E {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Tenths unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Tenths.of(mantissa.longValue());
    }
}
