package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.Units;

public class BigUnitsAdapter {

    public static BigDecimal marshal(Units obj) {
        return BigDecimal.valueOf(obj.getMantissa(), obj.scale());
    }

    public static <E extends Exception> Units unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : Units.of(num.unscaledValue().longValue());
    }
}
