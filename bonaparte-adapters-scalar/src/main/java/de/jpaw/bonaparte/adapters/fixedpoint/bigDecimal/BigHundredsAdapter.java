package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.Hundreds;

public class BigHundredsAdapter {

    public static BigDecimal marshal(Hundreds obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.scale());
    }

    public static <E extends Exception> Hundreds unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : Hundreds.of(num.unscaledValue().longValue());
    }
}
