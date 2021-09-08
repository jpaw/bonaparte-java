package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.MicroUnits;

public class BigMicroUnitsAdapter {

    public static BigDecimal marshal(MicroUnits obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.scale());
    }

    public static <E extends Exception> MicroUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : MicroUnits.of(num.unscaledValue().longValue());
    }
}
