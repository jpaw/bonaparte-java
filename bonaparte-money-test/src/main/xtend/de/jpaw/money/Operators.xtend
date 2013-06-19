package de.jpaw.money

import de.jpaw.money.BonaMoney
import java.math.BigDecimal

/** Syntactic sugar for the BonaMoney class when used from xtend */
public class BonaMoneyOperators {
    def static operator_plus(BonaMoney a, BonaMoney b) {
        a.add(b)
    }
    def static operator_minus(BonaMoney a, BonaMoney b) {
        a.subtract(b)
    }
    def static operator_multiply(BigDecimal a, BonaMoney b) {
        b.multiply(b.currency, a)
    }
    def static operator_multiply(BonaMoney a, BigDecimal b) {
        a.multiply(a.currency, b)
    }
    def boolean operator_equals(BonaMoney a, BonaMoney b) {
        if (a == null)
            b == null
        else
            a.equals(b)
    }
    def boolean operator_notEquals(BonaMoney a, BonaMoney b) {
        if (a == null)
            b != null
        else
            !a.equals(b)
    }

    // allow for BigDecimal * BonaCurrency as a shorthand to create a BonaMoney instance
    def static operator_multiply(BigDecimal amount, BonaCurrency curr) {
        new BonaMoney(curr, false, amount)
    }

    // allow for BigDecimal * String as a shorthand to create a BonaMoney instance
    def static operator_multiply(BigDecimal amount, String iso4217CurrencyCode) {
        new BonaMoney(new BonaCurrency(iso4217CurrencyCode), false, amount)
    }
}
