package de.jpaw.money;

import java.math.BigDecimal;

/** Syntactic sugar for the BonaMoney class when used from xtend */
public class BonaMoneyOperators {
    static public BonaMoney operator_plus(BonaMoney a, BonaMoney b) throws MonetaryException {
        return a.add(b);
    }
    static public BonaMoney operator_minus(BonaMoney a, BonaMoney b) throws MonetaryException {
        return a.subtract(b);
    }
    static public BonaMoney operator_multiply(BigDecimal a, BonaMoney b) throws MonetaryException {
        return b.multiply(b.getCurrency(), a);
    }
    static public BonaMoney operator_multiply(BonaMoney a, BigDecimal b) throws MonetaryException {
        return a.multiply(a.getCurrency(), b);
    }
    static public boolean operator_equals(BonaMoney a, BonaMoney b) {
        return (a == null) ? b == null : a.equals(b);
    }
    static public boolean operator_notEquals(BonaMoney a, BonaMoney b) {
        return (a == null) ? b != null : !a.equals(b);
    }

    // allow for BigDecimal * BonaCurrency as a shorthand to create a BonaMoney instance
    static public BonaMoney operator_multiply(BigDecimal amount, BonaCurrency curr) throws MonetaryException {
        return new BonaMoney(curr, false, amount);
    }

    // allow for BigDecimal * String as a shorthand to create a BonaMoney instance
    static public BonaMoney operator_multiply(BigDecimal amount, String iso4217CurrencyCode) throws MonetaryException {
        return new BonaMoney(new BonaCurrency(iso4217CurrencyCode), false, amount);
    }

}
