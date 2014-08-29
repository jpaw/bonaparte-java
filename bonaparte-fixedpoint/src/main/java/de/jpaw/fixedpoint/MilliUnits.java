package de.jpaw.fixedpoint;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MilliUnits extends FixedPointBase {
    private static final long serialVersionUID = -3973421895055422944L;
    public static final int DECIMALS = 3;
    public static final long UNIT_MANTISSA = 1000;
    public static final MilliUnits ZERO = new MilliUnits(0);
    public static final MilliUnits ONE = new MilliUnits(UNIT_MANTISSA);
    
    public MilliUnits(long mantissa) {
        super(mantissa);
    }

    public static MilliUnits of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }
    
    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static MilliUnits of(BigDecimal number) {
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).scaleByPowerOfTen(DECIMALS).longValue());
    }
    
    @Override
    public MilliUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == getMantissa())
            return this;
        return new MilliUnits(mantissa);
    }

    @Override
    public int getDecimals() {
        return DECIMALS;
    }

    @Override
    public MilliUnits getZero() {
        return ZERO;
    }

    @Override
    public MilliUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }
}
