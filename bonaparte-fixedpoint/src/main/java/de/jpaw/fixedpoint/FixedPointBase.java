package de.jpaw.fixedpoint;

import java.io.Serializable;
import java.math.BigDecimal;

/** Base class for fixed point arithmetic, using an implicitly scaled long value.
 * There are subclasses per number of decimals (from 0 to 9), and a variable scale
 * class, which stores the scale in a separate instance variable.
 *  
 * Instances of this class are immutable.
 * 
 * @author Michael Bischoff
 *
 */
public abstract class FixedPointBase implements Serializable, Comparable<FixedPointBase> {
    private static final long serialVersionUID = 8834214052987561284L;
    protected final static long [] powersOfTen = {
            1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L,
            1000000000000000000L
    };
    private final long mantissa;
    
    protected FixedPointBase(long mantissa) {
        this.mantissa = mantissa;
    }
    
    /** Returns a fixed point value object which has the same number of decimals as this, with a given mantissa.
     * This implementation returns cached instances for 0 and 1. Otherwise, in case this has the same mantissa, this is returned. */
    public abstract FixedPointBase newInstanceOf(long mantissa);
    
    /** Get the number of decimals. */
    public abstract int getDecimals();
    
    /** Get the number 0 in the same scale. */
    public abstract FixedPointBase getZero(); 
    
    /** Get the number 1 in the same scale. */
    public abstract FixedPointBase getUnit(); 
    
    /** Get the value representing the number 1. */
    public abstract long getUnitAsLong(); 
    
    /** Get the mantissa of this number as a primitive long. */
    public long getMantissa() {
        return mantissa;
    }
    
    /** Returns the value in a human readable form. */
    @Override
    public String toString() {
        return BigDecimal.valueOf(mantissa, getDecimals()).toPlainString();
    }
    
    @Override
    public int hashCode() {
        return 31 * getDecimals() + (int)(mantissa ^ mantissa >>> 32);
    }
    
    /** As with BigDecimal, equals returns true only of both objects are identical in all aspects. Use compareTo for numerical identity. */
    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof FixedPointBase))
            return false;
        if (that == this)
            return true;
        FixedPointBase _that = (FixedPointBase)that;
        return getDecimals() == _that.getDecimals() && mantissa == _that.mantissa && this.getClass() == that.getClass();
    }
    
    /** Returns the absolute value of this, using the same type and scale. */
    public FixedPointBase abs() {
        if (mantissa >= 0)
            return this;
        return newInstanceOf(-mantissa);
    }
    
    /** Returns a number with the opposite sign. */
    public FixedPointBase negate() {
        return newInstanceOf(-mantissa);
    }
    /** Xtend syntax sugar. unary minus maps to the negate method. */
    public FixedPointBase operator_minus() {
        return negate();
    }
    
    /** Returns the signum of this number, -1, 0, or +1. */
    public int signum() {
        return Long.signum(mantissa);
    }
    
    /** Returns true if this is numerically equivalent to 1. */
    public boolean isOne() {
        return mantissa == getUnitAsLong();
    }
    
    /** Returns true if this is numerically equivalent to -1. */
    public boolean isMinusOne() {
        return mantissa == -getUnitAsLong();
    }
    
    /** Returns true if this is not 0. */
    public boolean isNotZero() {
        return mantissa != 0;
    }
    
    /** Returns true if this is 0. */
    public boolean isZero() {
        return mantissa == 0;
    }
    /** Xtend syntax sugar. not maps to the isZero method. */
    public boolean operator_not() {
        return mantissa == 0;
    }
    
    /** Returns a unit in the last place. */
    public FixedPointBase ulp() {
        return newInstanceOf(1);
    }
    
    /** Returns the number scaled by 0.01, by playing with the scale (if possible). */
    public VariableUnits percent() {
        switch (getDecimals()) {
        case 18:
            return VariableUnits.valueOf(mantissa / 100, 18);
        case 17:
            return VariableUnits.valueOf(mantissa / 10, 18);
        default:  // 0 .. 16 decimals
            return VariableUnits.valueOf(mantissa, getDecimals() + 2);
        }
    }
    
    /** Returns the signum of this number, -1, 0, or +1.
     * Special care is taken in this implementation to work around any kind of integral overflows. */
    @Override
    public int compareTo(FixedPointBase that) {
        // first check is on signum only, to avoid incorrect responses due to integral overflow (MIN_VALUE must be < than MAX_VALUE)
        final int signumThis = Long.signum(this.mantissa);
        final int signumThat = Long.signum(that.mantissa);
        if (signumThis != signumThat) {
            // simple case, number differs by sign already
            return signumThis < signumThat ? -1 : 1;
        }
        if (signumThat == 0)
            return 0; // both are 0
        // here, both are either negative or positive
        // medium difficulty: they have the same scale
        int scaleDiff = this.getDecimals() - that.getDecimals();
        if (scaleDiff == 0) {
            // simple: compare the mantissas
            if (this.mantissa == that.mantissa)
                return 0;
            return this.mantissa < that.mantissa ? -1 : 1;
        }
        // both operands have the same sign, but differ in scaling. Scale down first, and only if the numbers then are the same, scale up
        if (scaleDiff < 0) {
            long diff = mantissa - that.mantissa / powersOfTen[-scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa * powersOfTen[-scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        } else {
            long diff = mantissa  / powersOfTen[scaleDiff] - that.mantissa;
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            // scaled difference is 0. In this case, scaling up cannot result in an overflow.
            diff = mantissa - that.mantissa * powersOfTen[scaleDiff];
            if (diff != 0)
                return diff < 0 ? -1 : 1;
            return 0;
        }
    }
    /** Xtend syntax sugar. spaceship maps to the compareTo method. */
    public int operator_spaceship(FixedPointBase that) {
        return compareTo(that);
    }
    public boolean operator_equals(FixedPointBase that) {
        return compareTo(that) == 0;
    }
    public boolean operator_notEquals(FixedPointBase that) {
        return compareTo(that) != 0;
    }
    public boolean operator_lessThan(FixedPointBase that) {
        return compareTo(that) < 0;
    }
    public boolean operator_lessEquals(FixedPointBase that) {
        return compareTo(that) <= 0;
    }
    public boolean operator_greaterThan(FixedPointBase that) {
        return compareTo(that) > 0;
    }
    public boolean operator_greaterEquals(FixedPointBase that) {
        return compareTo(that) >= 0;
    }
    
    /** Returns the smaller of this and the parameter. */
    public FixedPointBase min(FixedPointBase that) {
        return this.compareTo(that) <= 0 ? this : that;
    }
    
    /** Returns the bigger of this and the parameter. */
    public FixedPointBase max(FixedPointBase that) {
        return this.compareTo(that) >= 0 ? this : that;
    }
    
    /** Multiplies a fixed point number by an integral factor. The scale (and type) of the product is the same as the one of this. */
    public FixedPointBase multiply(int factor) {
        return newInstanceOf(mantissa * factor);
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public FixedPointBase operator_multiply(int factor) {
        return multiply(factor);
    }
    
    /** Multiplies a fixed point number by an another one. The type / scale of the result is undefined. */
    public FixedPointBase multiply(FixedPointBase that) {
        if (mantissa == 0)
            return this;                // 0 * x = 0
        if (that.mantissa == 0)
            return that;                // x * 0 = 0
        if (isOne())
            return that;                // 1 * x = x
        if (isMinusOne())
            return that.negate();       // -1 * x = -x
        if (that.isOne())
            return this;                // x * 1 = x
        if (that.isMinusOne())
            return this.negate();       // x * -1 = -x
        return null;  // FIXME
    }
    /** Xtend syntax sugar. multiply maps to the multiply method. */
    public FixedPointBase operator_multiply(FixedPointBase that) {
        return multiply(that);
    }
    
    /** Adds two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase add(FixedPointBase that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (mantissa == 0)
            return that;
        if (that.mantissa == 0)
            return this;
        int diff = this.getDecimals() - that.getDecimals();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa + powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(that.mantissa + powersOfTen[-diff] * this.mantissa);
    }
    /** Xtend syntax sugar. plus maps to the add method. */
    public FixedPointBase operator_plus(FixedPointBase that) {
        return add(that);
    }
    
    /** Subtracts two fixed point numbers. The scale (and type) of the sum is the bigger of the operand scales. */
    public FixedPointBase subtract(FixedPointBase that) {
        // first checks, if we can void adding the numbers and return either operand.
        if (that.mantissa == 0)
            return this;
        if (mantissa == 0)
            return that.negate();
        int diff = this.getDecimals() - that.getDecimals();
        if (diff >= 0)
            return this.newInstanceOf(this.mantissa - powersOfTen[diff] * that.mantissa);
        else
            return that.newInstanceOf(-that.mantissa + powersOfTen[-diff] * this.mantissa);
    }
    /** Xtend syntax sugar. minus maps to the subtract method. */
    public FixedPointBase operator_minus(FixedPointBase that) {
        return subtract(that);
    }

    /** Divides a number by an integer. */
    public FixedPointBase divide(int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1)
            return this;
        if (divisor == -1)
            return this.negate();
        return newInstanceOf(mantissa / divisor); 
    }
    /** Xtend syntax sugar. divide maps to the divide method. */
    public FixedPointBase operator_divide(int divisor) {
        return divide(divisor);
    }

    /** Computes the remainder of a division by an integer. */
    public FixedPointBase remainder(int divisor) {
        if (divisor == 0)
            throw new ArithmeticException("Division by 0");
        if (divisor == 1 || divisor == -1)
            return this.getZero();
        if (divisor == -1)
            return this.negate();
        long quotient = mantissa / divisor;
        return newInstanceOf(mantissa - quotient * divisor); 
    }
    /** Xtend syntax sugar. modulo maps to the remainder method. */
    public FixedPointBase operator_modulo(int divisor) {
        return remainder(divisor);
    }

}
