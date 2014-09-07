package de.jpaw.adapters.fpmoney;

import de.jpaw.api.iso.CurrencyDataProvider;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.fpmoney.FpCurrency;
import de.jpaw.fixedpoint.FixedPointSelector;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpCurrencyAdapter {
    public static CurrencyDataProvider dataProvider = JavaCurrencyDataProvider.instance;

    /** Convert the custom type into a serializable BonaPortable. */
    public static FpCurrency toBonaPortable(FPCurrency currency) {
        return new FpCurrency(currency.getCurrencyCode(), currency.getDecimals());
    }
    
    /** Convert a parsed adapter type into the custom type. 
     * @throws E */
    public static <E extends Exception> FPCurrency fromBonaPortable(BonaPortable obj, MessageParser<E> p) throws E {
        if (obj instanceof FpCurrency) {
            FpCurrency currency = (FpCurrency)obj;
            try {
                return new FPCurrency(
                        dataProvider.get(currency.getCurrencyCode()),
                        FixedPointSelector.getZeroForScale(currency.getDecimals())
                        );
            } catch (Exception e) {
                throw p.customExceptionConverter("FPCurrency(" + currency.getCurrencyCode() + ", " + currency.getDecimals() + ") not accepted", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }
}
