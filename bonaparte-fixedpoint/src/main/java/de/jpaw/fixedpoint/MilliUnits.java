package de.jpaw.fixedpoint;

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
