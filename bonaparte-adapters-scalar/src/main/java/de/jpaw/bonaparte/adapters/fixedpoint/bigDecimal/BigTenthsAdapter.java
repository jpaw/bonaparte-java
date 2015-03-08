package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.Tenths;

public class BigTenthsAdapter {

    public static BigDecimal marshal(Tenths obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }

    public static <E extends Exception> Tenths unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : Tenths.of(num.unscaledValue().longValue());
    }
}
