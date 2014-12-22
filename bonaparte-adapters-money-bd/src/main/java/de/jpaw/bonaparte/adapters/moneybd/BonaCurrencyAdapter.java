package de.jpaw.bonaparte.adapters.moneybd;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.bonaparte.pojos.adapters.moneybd.BCurrency;
import de.jpaw.money.BonaCurrency;
import de.jpaw.money.MonetaryException;

public class BonaCurrencyAdapter {
    /** Convert the custom type into a serializable BonaPortable. */
    public static BCurrency marshal(BonaCurrency currency) {
        return new BCurrency(currency.getCurrencyCode(), currency.getDecimals());
    }
    
    /** Convert a parsed adapter type into the custom type. */
    public static <E extends Exception> BonaCurrency unmarshal(BonaPortable obj, ExceptionConverter<E> p) throws E {
        if (obj instanceof BCurrency) {
            BCurrency currency = (BCurrency)obj;
            try {
                return new BonaCurrency(
                    currency.getCurrencyCode(),
                    currency.getDecimals()
                    );
            } catch (MonetaryException e) {
            	throw p.customExceptionConverter("Cannot convert " + obj + " to BonaCurrency", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }

}
