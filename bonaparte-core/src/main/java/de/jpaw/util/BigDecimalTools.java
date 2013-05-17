package de.jpaw.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** A class which provides some support functions which simplify working with BigDecimals.
 * The key issue we try to solve here is to provide a semantic where 2.5 == 2.50, while
 * the default BigDecimal implementation assumes 2.5 <> 2.50 (due to different scaling).
 * As bonaparte specifies a specific number of fractional digits, we scale to the bonaparte size
 * for hashCode().  The rounding mode is selected so that no Exception occurs.
 * As a consequence, we have to provide an implementation of equals, which is consistent with that (in contrast to using compareTo).
 *
 */
public class BigDecimalTools {
    
    /** Scales the BigDecimal to some predefined scale */
    static public BigDecimal scale(BigDecimal a, int decimals) {
        if (a != null && a.scale() != decimals)
            a = a.setScale(decimals, RoundingMode.HALF_EVEN);
        return a;
    }
    
    /** Computes the hashCode of a BigDecimal at a specific scale. */
    static public int hashCode(BigDecimal a, int decimals) {
        if (a == null)
            return 0;
        return scale(a, decimals).hashCode();
    }
    
    /** Compares to BigDecimal numbers. They can only be the same, if their rounded values are the same. */
    static public boolean equals(BigDecimal a, int aDecimals, BigDecimal b, int bDecimals) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;  // exactly one of them if null, the other not
        return scale(a, aDecimals).compareTo(scale(b, bDecimals)) == 0;
    }
}
