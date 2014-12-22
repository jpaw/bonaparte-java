package de.jpaw.bonaparte.adapters.fixedpoint.bigDecimal;

import java.math.BigDecimal;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.types.Units;

public class BigUnitsAdapter {

    public static <E extends Exception> BigDecimal marshal(Units obj) throws E {
        return BigDecimal.valueOf(obj.getMantissa(), obj.getScale());
    }
    
    public static <E extends Exception> Units unmarshal(BigDecimal num, ExceptionConverter<E> p) throws E {
        return num == null ? null : Units.of(num.unscaledValue().longValue());
    }
}
