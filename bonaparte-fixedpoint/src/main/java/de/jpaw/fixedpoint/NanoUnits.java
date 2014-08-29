package de.jpaw.fixedpoint;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NanoUnits extends FixedPointBase {
    private static final long serialVersionUID = -1254907575668729575L;
    public static final int DECIMALS = 9;
    public static final long UNIT_MANTISSA = 1000000000;
    public static final NanoUnits ZERO = new NanoUnits(0);
    public static final NanoUnits ONE = new NanoUnits(UNIT_MANTISSA);
    
    public NanoUnits(long mantissa) {
        super(mantissa);
    }

    public static NanoUnits of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }

    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static NanoUnits of(BigDecimal number) {
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).scaleByPowerOfTen(DECIMALS).longValue());
    }

    @Override
    public NanoUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == getMantissa())
            return this;
        return new NanoUnits(mantissa);
    }

    @Override
    public int getDecimals() {
        return DECIMALS;
    }

    @Override
    public NanoUnits getZero() {
        return ZERO;
    }

    @Override
    public NanoUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }
}
