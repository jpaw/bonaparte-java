package de.jpaw.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/** Class to store the notion of a currency, with the option to override the number of decimals (fractional digits).
 * By default, the number of decimals corresponds to the one of the real currency as defined by ISO 4217.
 */
public final class BonaCurrency implements Serializable {
    private static final long serialVersionUID = -6269291861207854200L;

    /**
     * Defines the maximum allowable number of decimal digits for monetary amounts. The maximum used by a real currency is 3 (TND and other Dinars), but we
     * allow for 6 in order to support potential virtual currencies as well as unit values with a higher precision.
     */
    public static final int MAX_DECIMALS = 6;

    /** Scaled zeroes holds a cached array of zero with respect to the different scales.
     * This is purely for speed optimization.
     */
    static private final BigDecimal [] SCALED_ZEROES;       // provides a preinitialized array of a few zeroes
    static {
        SCALED_ZEROES = new BigDecimal [BonaCurrency.MAX_DECIMALS + 1];
        SCALED_ZEROES[0] = BigDecimal.ZERO;
        for (int i = 1; i <= MAX_DECIMALS; ++i)
            SCALED_ZEROES[i] = BigDecimal.ZERO.setScale(i);
    }

    /** Smallest unit holds a cached array of the smallest representable unit with respect to the different scales.
     * This is purely for speed optimization.
     */
    static private final BigDecimal [] SMALLEST_UNITS;       // provides a preinitialized array of a few smallest units
    static {
        SMALLEST_UNITS = new BigDecimal [BonaCurrency.MAX_DECIMALS + 1];
        SMALLEST_UNITS[0] = BigDecimal.ONE;
        for (int i = 1; i <= MAX_DECIMALS; ++i)
            SMALLEST_UNITS[i] = BigDecimal.valueOf(1L, i);
    }

    /**
     * A string which defines the currency in a human readable form. It has always 3 letters and must match the ISO 4217 code in case of real currencies.
     *
     */
    private final String currencyCode;

    private final int decimals;

    public BonaCurrency(String currencyCode, int decimals) throws MonetaryException {
        if ((decimals < 0) || (decimals > MAX_DECIMALS)) {
            throw new MonetaryException(MonetaryException.ILLEGAL_NUMBER_OF_DECIMALS);
        }
        if ((currencyCode == null) || (currencyCode.length() != 3)) {
            throw new MonetaryException(MonetaryException.ILLEGAL_CURRENCY_CODE);
        }
        if (!currencyCode.equals(currencyCode.toUpperCase())) {
            throw new MonetaryException(MonetaryException.ILLEGAL_CURRENCY_CODE);
        }
        this.decimals = decimals;
        this.currencyCode = currencyCode;
    }

    public BonaCurrency(String currencyCode4217) throws MonetaryException {
        if ((currencyCode4217 == null) || (currencyCode4217.length() != 3)) {
            throw new MonetaryException(MonetaryException.ILLEGAL_CURRENCY_CODE);
        }
        if (!currencyCode4217.equals(currencyCode4217.toUpperCase())) {
            throw new MonetaryException(MonetaryException.ILLEGAL_CURRENCY_CODE);
        }
        this.currencyCode = currencyCode4217;
        try {
            this.decimals = Currency.getInstance(currencyCode4217).getDefaultFractionDigits();
            if ((decimals < 0) || (decimals > MAX_DECIMALS)) {
                throw new MonetaryException(MonetaryException.ILLEGAL_NUMBER_OF_DECIMALS);
            }
        } catch (IllegalArgumentException e) {
            throw new MonetaryException(MonetaryException.NOT_AN_ISO4217_CODE, currencyCode4217);
        }
    }

    /** Returns a BigDecimal representing 0 in the currency's scale. */
    public BigDecimal getZero() {
        return SCALED_ZEROES[decimals];
    }

    /** Returns a BigDecimal representing the smallest number greater than 0. */
    public BigDecimal getSmallestUnit() {
        return SMALLEST_UNITS[decimals];
    }

    /** scale the provided amount to the scale as defined in this currency. */
    public BigDecimal scale(BigDecimal amount, RoundingMode roundingMode) throws MonetaryException {
        if (amount.scale() == decimals)
            return amount;  // unchanged, shortcut
        try {
            return amount.setScale(decimals, roundingMode);
        } catch (ArithmeticException e) {
            throw new MonetaryException(MonetaryException.ROUNDING_PROBLEM);
        }
    }

    public String toShortString() {
        return currencyCode + ":" + decimals;
    }

    /** A scaling and error distribution method with the following properties.
     * Input is an array of numbers, which fulfills the condition that array element 0 is the sum of the others.
     * Desired output is an array with the same condition, plus all values scaled to this currency's scale.
     *
     * The implemented algorithm performs the rounding subject to the following conditions:
     * The resulting difference between the unrounded value and the round value are strictly less than the smallest possible
     * unit in this currency. (This implies that for every index i, there is a rounding strategy ri such that
     *  scaled[i] = unscaled[i].scale(decimals, ri).
     *
     * As an initial strategy, the banker's rounding (aka Gaussian rounding / twopenny rounding) for all elements is performed.
     * If the scaled sum matches, that result is returned.
     * Otherwise, elements are picked for a different rounding strategy in order of increasing relative error.
     *
     * @param unscaledAmounts
     * @return scaled values
     * @throws MonetaryException
     */
    public BigDecimal [] roundWithErrorDistribution(BigDecimal [] unscaledAmounts) throws MonetaryException {
        int n = unscaledAmounts.length;
        BigDecimal scaledAmounts [] = new BigDecimal [n];
        BigDecimal sum = getZero();
        for (int i = 0; i < n; ++i) {
            scaledAmounts[i] = scale(unscaledAmounts[i], RoundingMode.HALF_EVEN);
            if (i > 0)
                sum = sum.add(scaledAmounts[i]);
            // System.out.println("element["+i+"] = " + scaledAmounts[i].toPlainString());
        }
        int compareSign = scaledAmounts[0].compareTo(sum);  // > 0: rounded sum is bigger than sum of elements => increment elements
        if (compareSign == 0)
            return scaledAmounts;  // we are done
        // error distribution is required.

        BigDecimal singleAdjustment = getSmallestUnit();
        if (compareSign < 0)
            singleAdjustment = singleAdjustment.negate();
        BigDecimal difference = scaledAmounts[0].subtract(sum);  // this difference is to add to the elements / subtract from the sum
        // System.out.println("compareSign=" + compareSign + ", difference="+difference.toPlainString());
        boolean [] isEligible = new boolean [n];
        double [] relativeError = new double [n];
        int numberEligible = 0;
        int numberToAdjust = difference.abs().scaleByPowerOfTen(decimals).intValue();
        assert numberToAdjust > 0 && numberToAdjust < n : "Unexplainable number of elements to adjust";
        for (int i = 0; i < n; ++i) {
            isEligible[i] = unscaledAmounts[i].compareTo(scaledAmounts[i]) == (i > 0 ? compareSign : -compareSign);
            if (isEligible[i]) {
                // relative error is <= 1 by definition: if unscaled <= 0.5: diff = unscaled, else unscaled > 0.5 and therefore > diff
                relativeError[i] =
                    Math.abs(unscaledAmounts[i].subtract(scaledAmounts[i]).doubleValue()) /
                    Math.abs(unscaledAmounts[i].doubleValue());
                ++numberEligible;
            } else {
                relativeError[i] = 0.0;  // just to avoid errorneous access
            }
            // System.out.println("eligible["+i+"] = " + isEligible[i] + ", relative error = " + relativeError[i]);
        }
        assert numberEligible >= numberToAdjust : "Did not find enough adjustable elements";
        while (numberToAdjust > 0) {
            // find a remaining eligible element with the smallest relativeError
            double minError = 2.0;  // anything > 1.0 shold do
            int smallestIndex = -1;
            for (int i = 0; i < n; ++i) {
                if (isEligible[i] && relativeError[i] < minError) {
                    minError = relativeError[i];
                    smallestIndex = i;
                }
            }
            assert(smallestIndex >= 0);             // did actually find one
            isEligible[smallestIndex] = false;      // mark it "used"
            if (smallestIndex > 0)
                scaledAmounts[smallestIndex] = scaledAmounts[smallestIndex].add(singleAdjustment);
            else
                scaledAmounts[smallestIndex] = scaledAmounts[smallestIndex].subtract(singleAdjustment);
            --numberToAdjust;
        }
        return scaledAmounts;
    }


    // default Eclipse autogenerated methods below

    public String getCurrencyCode() {
        return currencyCode;
    }

    public int getDecimals() {
        return decimals;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((currencyCode == null) ? 0 : currencyCode.hashCode());
        result = (prime * result) + decimals;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BonaCurrency other = (BonaCurrency) obj;
        if (currencyCode == null) {
            if (other.currencyCode != null) {
                return false;
            }
        } else if (!currencyCode.equals(other.currencyCode)) {
            return false;
        }
        if (decimals != other.decimals) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BonaCurrency[currencyCode=" + currencyCode + ", decimals=" + decimals + "]";
    }

}
