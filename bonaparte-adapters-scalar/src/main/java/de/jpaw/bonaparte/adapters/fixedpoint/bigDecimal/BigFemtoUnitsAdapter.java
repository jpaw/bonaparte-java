package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.FemtoUnits;

public class BigFemtoUnitsAdapter {

    public static <E extends Exception> BigDecimal marshal(FemtoUnits obj) throws E {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }
    
    public static <E extends Exception> FemtoUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : FemtoUnits.of(num.unscaledValue().longValue());
    }
}
