package de.jpaw.bonaparte.adapters.fixedpoint;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.bonaparte.pojos.adapters.fixedpoint.HundredsAd;

public class HundredsAdapter {

    public static <E extends Exception> Long marshal(HundredsAd obj) throws E {
        return obj.getMantissa();
    }
    
    public static <E extends Exception> de.jpaw.fixedpoint.types.Hundreds unmarshal(Long mantissa, ExceptionConverter<E> p) throws E {
        return mantissa == null ? null : de.jpaw.fixedpoint.types.Hundreds.of(mantissa.longValue());
    }
}
