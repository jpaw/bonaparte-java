package de.jpaw.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.collect.ImmutableList;

/**
 * The BonaMoney class provides a series of functionality useful for working with monetary amounts.
 * These include scale normalization, and operations with rounding.
 * Any instance of this class ensures the following properties:
 * The sum of net and tax amounts equal the gross amount.
 * All amounts are of same scale, and therefore can be compared with equals().
 * All amounts are of same sign (all >= 0 or all <= 0)
 * Any rounding is done with appropriate error distribution. 
 * @author BISC02
 * 
 */
public final class BonaMoney implements Serializable, MoneyGetter {
    private static final long serialVersionUID = 6269291861207854500L;

    static private final BigDecimal [] EMPTY_ARRAY = new BigDecimal[0];
    static private final ImmutableList<BigDecimal> EMPTY_LIST = ImmutableList.of();
    
    private final BonaCurrency currency;

    /** Specifies the number of VAT amounts in this object. If 0, then we have a single amount here. */
    private final int numTaxAmounts;

    private final BigDecimal grossAmount;
    private final BigDecimal netAmount;
    private final ImmutableList<BigDecimal> taxAmounts;

    /** Constructor for a single amount */
    public BonaMoney(BonaCurrency currency, boolean allowRounding, BigDecimal amount) throws MonetaryException {
        this.currency = currency;
        this.numTaxAmounts = 0;
        this.grossAmount = currency.scale(amount, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        this.netAmount = this.grossAmount;
        taxAmounts = EMPTY_LIST;
    }

    /** Creates a BonaMoney instance of value 0 for the given currency. */
    public BonaMoney(BonaCurrency currency) {
        this.currency = currency;
        this.numTaxAmounts = 0;
        this.grossAmount = currency.getZero();
        this.netAmount = this.grossAmount;
        this.taxAmounts = EMPTY_LIST;
    }
        
    public BonaMoney(BonaCurrency currency, boolean allowRounding, BigDecimal grossAmount, BigDecimal netAmount, BigDecimal ... tax)
            throws MonetaryException {
        this.currency = currency;
        this.numTaxAmounts = tax.length;
        
        // Plausibility checks. Throw an exception if anything looks weird, better than trying to find rounding problems later.
        if (grossAmount == null && netAmount == null)
            throw new MonetaryException(MonetaryException.UNDEFINED_AMOUNTS);
        // plausi check: if all amounts are provided, the sum must match!
        BigDecimal sum = BigDecimal.ZERO;
        boolean useNegatives = false;
        boolean usePositives = false;
        for (BigDecimal t : tax) {
            sum = sum.add(t);
            int sign = t.signum();
            if (sign < 0)
                useNegatives = true; 
            if (sign > 0)
                usePositives = true; 
        }
        if (netAmount != null) {
            sum = sum.add(netAmount);
            // now grossAmount is set to net + taxes
            if (grossAmount != null) {
                if (grossAmount.compareTo(sum) != 0)
                    throw new MonetaryException(MonetaryException.SUM_MISMATCH, grossAmount.toPlainString() + " <> " + sum.toPlainString());
            } else {
                // net <> null, but gross => define gross by sum of all amounts
                grossAmount = sum;
            }
        } else {
            // netAmount is null, but gross not (by prior check). Define net to be the difference!
            netAmount = grossAmount.subtract(sum);
        }
        // now check if all signs are the same. The missing one is the net sign.
        int sign = netAmount.signum();
        if (sign < 0)
            useNegatives = true; 
        if (sign > 0)
            usePositives = true;
        if (usePositives && useNegatives)
            throw new MonetaryException(MonetaryException.SIGNS_DIFFER);
        // we're good so far. Now see if there is any rounding issue. If rounding is disable, this is an easy job. Shortcut this.
        try {
            ImmutableList.Builder<BigDecimal> b = ImmutableList.builder();
            if (!allowRounding) {
                for (BigDecimal t : tax)
                    b.add(currency.scale(t, RoundingMode.UNNECESSARY));
                this.taxAmounts = b.build();
                this.netAmount = currency.scale(netAmount, RoundingMode.UNNECESSARY);
                this.grossAmount = currency.scale(grossAmount, RoundingMode.UNNECESSARY);
            } else {
                // complex case? Scaling could lead to a difference, which then needs to be allocated to the elements.
                BigDecimal roundedGross = currency.scale(grossAmount, RoundingMode.HALF_EVEN);
                BigDecimal roundedNet = currency.scale(netAmount, RoundingMode.HALF_EVEN);
                sum = roundedNet;
                for (BigDecimal t : tax) {
                    BigDecimal tmp = currency.scale(t, RoundingMode.HALF_EVEN); 
                    sum = sum.add(tmp);
                    b.add(tmp);
                }
                if (roundedGross.compareTo(sum) == 0) {
                    // great again! No issue.
                    this.taxAmounts = b.build();
                    this.netAmount = roundedNet;
                    this.grossAmount = roundedGross;
                } else {
                    // oh hell!
                    // TODO!  For now, we shortcut and put all difference to gross
                    this.taxAmounts = b.build();
                    this.netAmount = roundedNet;
                    this.grossAmount = sum;     // FIXME!
                }
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
    BonaMoney multiply(BonaCurrency targetCurrency, BigDecimal factor) throws MonetaryException {
        // shortcut if the new currency is the same as the old, and the fxRate is 1.0
        if (currency.equals(targetCurrency) && BigDecimal.ONE.compareTo(factor) == 0)
            return this;
        try {
            // OK, run the multiplication
            if (numTaxAmounts == 0) {
                // easy case, no allocation of any differences
                return new BonaMoney(targetCurrency, true, grossAmount.multiply(factor));
            }
            // at least one tax amount, possible rounding issues. Compute with stupid approach and delegate to constructor.
            BigDecimal taxes[] = new BigDecimal[numTaxAmounts];
            for (int i = 0; i < numTaxAmounts; ++i)
                taxes[i] = taxAmounts.get(i).multiply(factor);
            return new BonaMoney(targetCurrency, true, grossAmount.multiply(factor), netAmount.multiply(factor), taxes);
        } catch (MonetaryException e) {
            throw new MonetaryException(MonetaryException.UNEXPECTED_ROUNDING_PROBLEM, "Code " + e.getErrorCode()
                    + " while converting from " + currency.toShortString() + " to " + targetCurrency.toShortString()
                    + " with factor " + factor.toPlainString());
        }
    }

    /** Add two BonaMoney instances. Both operands must have the identical currency and the same number of tax amounts.
     * A check is performed, if all signs all still consistent after the operation. */
    BonaMoney add(BonaMoney augent) throws MonetaryException {
        if (!currency.equals(augent.getCurrency()) || numTaxAmounts != augent.getNumTaxAmounts())
            throw new MonetaryException(MonetaryException.INCOMPATIBLE_OPERANDS, "add: "
                    + currency.toShortString() + "-" + numTaxAmounts + " <> "
                    + augent.getCurrency().toShortString() + "-" + augent.getNumTaxAmounts());
        if (numTaxAmounts == 0) {
            // easy case again, no chance of differing signs
            return new BonaMoney(currency, false, grossAmount.add(augent.getGrossAmount()));
        }
        BigDecimal taxes[] = new BigDecimal[numTaxAmounts];
        for (int i = 0; i < numTaxAmounts; ++i)
            taxes[i] = taxAmounts.get(i).add(augent.getGrossAmount());
        return new BonaMoney(currency, false, grossAmount.add(augent.getGrossAmount()), netAmount.add(augent.getNetAmount()), taxes);
    }

    /** Subtract two BonaMoney instances. Both operands must have the identical currency and the same number of tax amounts.
     * A check is performed, if all signs all still consistent after the operation.
     * Essentially same code as add. */
    BonaMoney subtract(BonaMoney subtrahend) throws MonetaryException {
        if (!currency.equals(subtrahend.getCurrency()) || numTaxAmounts != subtrahend.getNumTaxAmounts())
            throw new MonetaryException(MonetaryException.INCOMPATIBLE_OPERANDS, "subtract: "
                    + currency.toShortString() + "-" + numTaxAmounts + " <> "
                    + subtrahend.getCurrency().toShortString() + "-" + subtrahend.getNumTaxAmounts());
        if (numTaxAmounts == 0) {
            // easy case again, no chance of differing signs
            return new BonaMoney(currency, false, grossAmount.subtract(subtrahend.getGrossAmount()));
        }
        BigDecimal taxes[] = new BigDecimal[numTaxAmounts];
        for (int i = 0; i < numTaxAmounts; ++i)
            taxes[i] = taxAmounts.get(i).subtract(subtrahend.getGrossAmount());
        return new BonaMoney(currency, false, grossAmount.subtract(subtrahend.getGrossAmount()), netAmount.subtract(subtrahend.getNetAmount()), taxes);
    }
    
    /** Stores the amounts of this instance in a mutable object (for example BonaPortable DTO).
     * The currency is skipped, due to most likely duplication. */ 
    public void storeAmounts(MoneySetter target) {
        target.setGrossAmount(grossAmount);
        target.setNetAmount(netAmount);
        target.setTaxAmounts(taxAmounts);
    }
    
    /** Factory method to create a new BonaMoney from a readable source of amounts.
     * If numTaxAmounts >= 0, expects the list to have exactly that many amounts, else (-1) don't care. 
     * @throws MonetaryException */
    public static BonaMoney fromAmounts(BonaCurrency currency, boolean allowRounding, int numTaxAmounts, boolean addMissingTaxAmounts, MoneyGetter source)
            throws MonetaryException {
        int got = source.getTaxAmounts().size();
        BigDecimal [] taxAmounts = numTaxAmounts == 0 ? EMPTY_ARRAY : new BigDecimal[numTaxAmounts];
        for (int i = 0; i < got; ++i)
            taxAmounts[i] = source.getTaxAmounts().get(i);
        if (numTaxAmounts != got) {
            // maybe a list extension is required
            if (!addMissingTaxAmounts || numTaxAmounts < got)
                // nope, too many, or extension not allowed
                throw new MonetaryException(MonetaryException.INCORRECT_NUMBER_TAX_AMOUNTS, "Want " + numTaxAmounts + ", got " + got);
            // Add some ZEROES
            for (int i = got; i < numTaxAmounts; ++i)
                taxAmounts[i] = currency.getZero();  // save later scaling by using a correctly scaled zero already now!
        }
        // no easy shortcut this time when numTaxAmounts = 0, because the source is unsecure, gross could be <> net
        return new BonaMoney(currency, allowRounding, source.getGrossAmount(), source.getNetAmount(), taxAmounts);
    }
    
    // autogenerated stuff below
    
    public BonaCurrency getCurrency() {
        return currency;
    }

    public int getNumTaxAmounts() {
        return numTaxAmounts;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public ImmutableList<BigDecimal> getTaxAmounts() {
        return taxAmounts;
    }

}
