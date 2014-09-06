package de.jpaw.adapters.money;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.pojos.adapters.money.BCurrency;
import de.jpaw.money.BonaCurrency;
import de.jpaw.money.MonetaryException;

public class BonaCurrencyAdapter {
    /** Convert the custom type into a serializable BonaPortable. */
    public static BCurrency toBonaPortable(BonaCurrency currency) {
        return new BCurrency(currency.getCurrencyCode(), currency.getDecimals());
    }
    
    /** Convert a parsed adapter type into the custom type. */
    public static BonaCurrency fromBonaPortable(BonaPortable obj) {
        if (obj instanceof BCurrency) {
            BCurrency currency = (BCurrency)obj;
            try {
                return new BonaCurrency(
                    currency.getCurrencyCode(),
                    currency.getDecimals()
                    );
            } catch (MonetaryException e) {
                throw new IllegalArgumentException("Cannot convert to BonaCurrency: " + e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }

}
