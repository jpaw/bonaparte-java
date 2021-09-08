package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.PicoUnits;

public class BigPicoUnitsAdapter {

    public static BigDecimal marshal(PicoUnits obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.scale());
    }

    public static <E extends Exception> PicoUnits unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : PicoUnits.of(num.unscaledValue().longValue());
    }
}
