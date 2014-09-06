package de.jpaw.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.collect.ImmutableList;

import de.jpaw.algebra.AbelianGroup;

/**
 * The BonaMoney class provides a series of functionality useful for working with monetary amounts.
 * These include scale normalization, and operations with rounding.
 * Any instance of this class ensures the following properties:
 * The sum of net and tax amounts equal the gross amount.
 * All amounts are of same scale, and therefore can be compared with equals().
 * All amounts are of same sign (all >= 0 or all <= 0)
 * Any rounding is done with appropriate error distribution.
 * @author Michael Bischoff
 *
 */
public final class BonaMoney implements Serializable, AbelianGroup<BonaMoney> {
    private static final long serialVersionUID = 6269291861207854500L;

//    static private final BigDecimal [] EMPTY_ARRAY = new BigDecimal[0];
    static private final ImmutableList<BigDecimal> EMPTY_LIST = ImmutableList.of();

    private final BonaCurrency currency;                        // the currency of this amount
    private final BigDecimal amount;                            // the main (gross) amount (or total)
    private final ImmutableList<BigDecimal> componentAmounts;   // net + taxes (or components)

    /** Constructor for a single amount */
    public BonaMoney(BonaCurrency currency, boolean allowRounding, BigDecimal amount) throws MonetaryException {
        this.currency = currency;
        this.amount = currency.scale(amount, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        componentAmounts = EMPTY_LIST;
    }

    /** Creates a BonaMoney instance of value 0 for the given currency. */
    public BonaMoney(BonaCurrency currency) {
        this.currency = currency;
        this.amount = currency.getZero();
        this.componentAmounts = EMPTY_LIST;
    }

    /** Constructor for a single with a breakdown of equal-sign components (for example net + taxes). */
    public BonaMoney(BonaCurrency currency, boolean allowRounding, boolean requireSameSign, BigDecimal amount, BigDecimal ... components)
            throws MonetaryException {
        this.currency = currency;
        if (components == null || components.length == 0) {
            // simple case: no breakdown given, same as above
            if (amount == null)
                throw new MonetaryException(MonetaryException.UNDEFINED_AMOUNTS);
            this.amount = currency.scale(amount, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
            this.componentAmounts = EMPTY_LIST;
            return;
        }

        if (requireSameSign) {
            boolean useNegatives = false;
            boolean usePositives = false;
            for (BigDecimal t : components) {
                int sign = t.signum();
                if (sign < 0)
                    useNegatives = true;
                if (sign > 0)
                    usePositives = true;
            }
            // now check if all signs are the same.
            if (usePositives && useNegatives)
                throw new MonetaryException(MonetaryException.SIGNS_DIFFER);
        }

        // plausi check: if all amounts are provided, the sum must match!
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal t : components) {
            sum = sum.add(t);
        }
        // Plausibility checks. Throw an exception if anything looks weird, better than trying to find rounding problems later.
        if (amount == null) {
            if (components.length == 0)
                throw new MonetaryException(MonetaryException.UNDEFINED_AMOUNTS);
            // set gross to sum
            amount = sum;
        } else {
            // if no components: fine, else the sum must match
            if (amount.compareTo(sum) != 0)
                throw new MonetaryException(MonetaryException.SUM_MISMATCH, amount.toPlainString() + " <> " + sum.toPlainString());
        }

        // we're good so far. Now see if there is any rounding issue. If rounding is disable, this is an easy job. Shortcut this.
        try {
            ImmutableList.Builder<BigDecimal> b = ImmutableList.builder();
            if (!allowRounding) {
                for (BigDecimal t : components)
                    b.add(currency.scale(t, RoundingMode.UNNECESSARY));
                this.componentAmounts = b.build();
                this.amount = currency.scale(amount, RoundingMode.UNNECESSARY);
            } else {
                // complex case? Scaling could lead to a difference, which then needs to be allocated to the elements.
                // we assign all values to some big array and delegate to the BonaCurrency class to do the heavy lifting
                BigDecimal [] unscaled = new BigDecimal [1 + components.length];
                unscaled[0] = amount;
                for (int i = 0; i < components.length; ++i)
                    unscaled[i + 1] = components[i];
                BigDecimal [] scaled = currency.roundWithErrorDistribution(unscaled);
                for (int i = 0; i < components.length; ++i)
                    b.add(scaled[i + 1]);
                this.componentAmounts = b.build();
                this.amount = scaled[0];
            }
        } catch (ArithmeticException e) {
            throw new MonetaryException(MonetaryException.ROUNDING_PROBLEM);
        }
    }

    /** Multiply with a scalar, possibly with different scaling.
     * Allocate any rounding differences.
     * Can be used for FX conversion (then factor is the fxRate) and for discounts (in which case rate <> 1.0, but currencies are the same), or for
     * unit price to final price conversion (in which case factor is the quantity). Finally, factor can be 1.0, but the currencies differ, in which case
     * rounding is desired.
     * @throws MonetaryException if */
    public BonaMoney multiply(BonaCurrency targetCurrency, BigDecimal factor) throws MonetaryException {
        // shortcut if the new currency is the same as the old, and the fxRate is 1.0
        if (currency.equals(targetCurrency) && BigDecimal.ONE.compareTo(factor) == 0)
            return this;
        try {
            // OK, run the multiplication
            if (componentAmounts.size() == 0) {
                // easy case, no allocation of any differences
                return new BonaMoney(targetCurrency, true, amount.multiply(factor));
            }
            // at least one component amount, possible rounding issues. Compute with stupid approach and delegate to constructor.
            BigDecimal components[] = new BigDecimal[componentAmounts.size()];
            for (int i = 0; i < componentAmounts.size(); ++i)
                components[i] = componentAmounts.get(i).multiply(factor);
            return new BonaMoney(targetCurrency, true, false, amount.multiply(factor), components);
        } catch (MonetaryException e) {
            throw new MonetaryException(MonetaryException.UNEXPECTED_ROUNDING_PROBLEM, "Code " + e.getErrorCode()
                    + " while converting from " + currency.toShortString() + " to " + targetCurrency.toShortString()
                    + " with factor " + factor.toPlainString());
        }
    }

    /** Add two BonaMoney instances. Both operands must have the identical currency and the same number of tax amounts.
     * A check is performed, if all signs all still consistent after the operation. */
    @Override
    public BonaMoney add(BonaMoney augent) throws MonetaryException {
        if (!currency.equals(augent.getCurrency()) || componentAmounts.size() != augent.getNumComponentAmounts())
            throw new MonetaryException(MonetaryException.INCOMPATIBLE_OPERANDS, "add: "
                    + currency.toShortString() + "-" + componentAmounts.size() + " <> "
                    + augent.getCurrency().toShortString() + "-" + augent.getNumComponentAmounts());
        if (componentAmounts.size() == 0) {
            // easy case again, no chance of differing signs
            return new BonaMoney(currency, false, amount.add(augent.amount));
        }
        BigDecimal taxes[] = new BigDecimal[componentAmounts.size()];
        for (int i = 0; i < componentAmounts.size(); ++i)
            taxes[i] = componentAmounts.get(i).add(augent.amount);
        return new BonaMoney(currency, false, false, amount.add(augent.amount), taxes);
    }

    /** Subtract two BonaMoney instances. Both operands must have the identical currency and the same number of tax amounts.
     * A check is performed, if all signs all still consistent after the operation.
     * Essentially same code as add. */
    @Override
    public BonaMoney subtract(BonaMoney subtrahend) throws MonetaryException {
        if (!currency.equals(subtrahend.getCurrency()) || componentAmounts.size() != subtrahend.componentAmounts.size())
            throw new MonetaryException(MonetaryException.INCOMPATIBLE_OPERANDS, "subtract: "
                    + currency.toShortString() + "-" + componentAmounts.size() + " <> "
                    + subtrahend.getCurrency().toShortString() + "-" + subtrahend.componentAmounts.size());
        if (componentAmounts.size() == 0) {
            // easy case again, no chance of differing signs
            return new BonaMoney(currency, false, amount.subtract(subtrahend.amount));
        }
        BigDecimal taxes[] = new BigDecimal[componentAmounts.size()];
        for (int i = 0; i < componentAmounts.size(); ++i)
            taxes[i] = componentAmounts.get(i).subtract(subtrahend.amount);
        return new BonaMoney(currency, false, false, amount.subtract(subtrahend.amount), taxes);
    }

//    /** Stores the amounts of this instance in a mutable object (for example BonaPortable DTO).
//     * The currency is skipped, due to most likely duplication. */
//    public void storeAmounts(MoneySetter target) {
//        target.setAmount(amount);
//        target.setComponentAmounts(componentAmounts);
//    }
//
//    /** Factory method to create a new BonaMoney from a readable source of amounts.
//     * If componentAmounts.size() >= 0, expects the list to have exactly that many amounts, else (-1) don't care.
//     * @throws MonetaryException */
//    public static BonaMoney fromAmounts(BonaCurrency currency, boolean allowRounding, int numTaxAmounts, boolean addMissingTaxAmounts, boolean requireSameSign,
//            MoneyGetter source)
//            throws MonetaryException {
//        int got = source.getComponentAmounts().size();
//        BigDecimal [] componentAmounts = numTaxAmounts == 0 ? EMPTY_ARRAY : new BigDecimal[numTaxAmounts];
//        for (int i = 0; i < got; ++i)
//            componentAmounts[i] = source.getComponentAmounts().get(i);
//        if (numTaxAmounts != got) {
//            // maybe a list extension is required
//            if (!addMissingTaxAmounts || numTaxAmounts < got)
//                // nope, too many, or extension not allowed
//                throw new MonetaryException(MonetaryException.INCORRECT_NUMBER_TAX_AMOUNTS, "Want " + numTaxAmounts + ", got " + got);
//            // Add some ZEROES
//            for (int i = got; i < numTaxAmounts; ++i)
//                componentAmounts[i] = currency.getZero();  // save later scaling by using a correctly scaled zero already now!
//        }
//        // no easy shortcut this time when numTaxAmounts = 0, because the source is unsecure, gross could be <> net
//        return new BonaMoney(currency, allowRounding, requireSameSign, source.getAmount(), componentAmounts);
//    }

    @Override
    public String toString() {
        StringBuilder a = new StringBuilder();
        a.append("BonaMoney[");
        a.append(currency.toString());
        a.append(", gross=");
        a.append(amount.toPlainString());
        if (componentAmounts.size() > 0) {
            a.append(", net&tax=(");
            for (int i = 0; i < componentAmounts.size(); ++i) {
                if (i > 0)
                    a.append(", ");
                a.append(componentAmounts.get(i).toPlainString());
            }
            a.append(")");
        }
        a.append("]");
        return a.toString();
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
        BonaMoney other = (BonaMoney) obj;
        if (componentAmounts.size() != other.componentAmounts.size() ||
                !currency.equals(other.currency) ||
                !amount.equals(other.amount))
            return false;
        for (int i = 0; i < componentAmounts.size(); ++i)
            if (!componentAmounts.get(i).equals(other.componentAmounts.get(i)))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = currency.hashCode();
        result = (prime * result) + amount.hashCode();
        for (int i = 0; i < componentAmounts.size(); ++i)
            result = (prime * result) + componentAmounts.get(i).hashCode();
        return result;
    }

    // autogenerated stuff below

    public BonaCurrency getCurrency() {
        return currency;
    }

    public int getNumComponentAmounts() {
        return componentAmounts.size();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ImmutableList<BigDecimal> getComponentAmounts() {
        return componentAmounts;
    }

    @Override
    public BonaMoney negate() throws MonetaryException {
        if (componentAmounts.size() == 0) {
            // just one number to negate, (if at all)
            if (amount.compareTo(BigDecimal.ZERO) == 0)
                return this;  // - 0 = 0
            return new BonaMoney(currency, false, amount.negate());
        }
        // BonaMoney with breakdown, more complex
        BigDecimal taxes[] = new BigDecimal[componentAmounts.size()];
        for (int i = 0; i < componentAmounts.size(); ++i)
            taxes[i] = componentAmounts.get(i).negate();
        return new BonaMoney(currency, false, false, amount.negate(), taxes);
    }

}
