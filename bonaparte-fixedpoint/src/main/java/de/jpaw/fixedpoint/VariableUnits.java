package de.jpaw.fixedpoint;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VariableUnits extends FixedPointBase {
    private static final long serialVersionUID = 8621674182590849295L;
    private final int scale;
    private final static VariableUnits [] ZEROs = {
            new VariableUnits(0L, 0),
            new VariableUnits(0L, 1),
            new VariableUnits(0L, 2),
            new VariableUnits(0L, 3),
            new VariableUnits(0L, 4),
            new VariableUnits(0L, 5),
            new VariableUnits(0L, 6),
            new VariableUnits(0L, 7),
            new VariableUnits(0L, 8),
            new VariableUnits(0L, 9),
            new VariableUnits(0L, 10),
            new VariableUnits(0L, 11),
            new VariableUnits(0L, 12),
            new VariableUnits(0L, 13),
            new VariableUnits(0L, 14),
            new VariableUnits(0L, 15),
            new VariableUnits(0L, 16),
            new VariableUnits(0L, 17),
            new VariableUnits(0L, 18)
    };
    private final static VariableUnits [] ONEs = {
            new VariableUnits(powersOfTen[0], 0),
            new VariableUnits(powersOfTen[1], 1),
            new VariableUnits(powersOfTen[2], 2),
            new VariableUnits(powersOfTen[3], 3),
            new VariableUnits(powersOfTen[4], 4),
            new VariableUnits(powersOfTen[5], 5),
            new VariableUnits(powersOfTen[6], 6),
            new VariableUnits(powersOfTen[7], 7),
            new VariableUnits(powersOfTen[8], 8),
            new VariableUnits(powersOfTen[9], 9),
            new VariableUnits(powersOfTen[10], 10),
            new VariableUnits(powersOfTen[11], 11),
            new VariableUnits(powersOfTen[12], 12),
            new VariableUnits(powersOfTen[13], 13),
            new VariableUnits(powersOfTen[14], 14),
            new VariableUnits(powersOfTen[15], 15),
            new VariableUnits(powersOfTen[16], 16),
            new VariableUnits(powersOfTen[17], 17),
            new VariableUnits(powersOfTen[18], 18)
    };

    private final static void scaleCheck(int scale) {
        if (scale < 0 || scale > 18)
            throw new RuntimeException("Illegal scale " + scale + ", must be in range [0,18]");
    }
    
    // This is certainly not be the most efficient implementation, as it involves the construction of up to 2 new BigDecimals
    // TODO: replace it by a zero GC version
    public static VariableUnits valueOf(BigDecimal number, int scale) {
        return valueOf(number.setScale(scale, RoundingMode.UNNECESSARY).scaleByPowerOfTen(scale).longValue(), scale);
    }
    
    /** Factory method. Similar to the constructor, but returns cached instances for 0 and 1. */
    public static VariableUnits valueOf(long mantissa, int scale) {
        scaleCheck(scale);
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == powersOfTen[scale])
            return ONEs[scale];
        return new VariableUnits(mantissa, scale);
    }
    
    public VariableUnits(long mantissa, int scale) {
        super(mantissa);
        this.scale = scale;
        scaleCheck(scale);
    }

    @Override
    public VariableUnits newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZEROs[scale];
        if (mantissa == powersOfTen[scale])
            return ONEs[scale];
        if (mantissa == getMantissa())
            return this;
        return new VariableUnits(mantissa, scale);
    }

    @Override
    public int getDecimals() {
        return scale;
    }

    @Override
    public VariableUnits getZero() {
        return ZEROs[scale];
    }

    @Override
    public VariableUnits getUnit() {
        return ONEs[scale];
    }

    @Override
    public long getUnitAsLong() {
        return powersOfTen[scale];
    }
}