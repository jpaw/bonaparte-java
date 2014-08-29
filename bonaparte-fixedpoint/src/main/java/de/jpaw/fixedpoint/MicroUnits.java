package de.jpaw.fixedpoint;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MicroUnits extends FixedPointBase {
    private static final long serialVersionUID = 4897338618191622754L;
    public static final int DECIMALS = 6;
    public static final long UNIT_MANTISSA = 1000000;
    public static final MicroUnits ZERO = new MicroUnits(0);
    public static final MicroUnits ONE = new MicroUnits(UNIT_MANTISSA);
    
    public MicroUnits(long mantissa) {
        super(mantissa);
    }

    public static MicroUnits of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }
    
    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static MicroUnits of(BigDecimal number) {
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).scaleByPowerOfTen(DECIMALS).longValue());
    }
    
    @Override
    public MicroUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == getMantissa())
            return this;
        return new MicroUnits(mantissa);
    }

    @Override
    public int getDecimals() {
        return DECIMALS;
    }

    @Override
    public MicroUnits getZero() {
        return ZERO;
    }

    @Override
    public MicroUnits getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

}
