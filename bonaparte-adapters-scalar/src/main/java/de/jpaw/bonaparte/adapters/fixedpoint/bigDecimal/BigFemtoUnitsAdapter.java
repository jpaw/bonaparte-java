package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.FemtoUnits;

public class BigFemtoUnitsAdapter {

    public static BigDecimal marshal(FemtoUnits obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.scale());
    }

    public static <E extends Exception> FemtoUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : FemtoUnits.of(num.unscaledValue().longValue());
    }
}
