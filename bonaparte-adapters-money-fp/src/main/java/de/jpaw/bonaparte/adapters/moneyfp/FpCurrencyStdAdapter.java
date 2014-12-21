package de.jpaw.bonaparte.adapters.moneyfp;

import de.jpaw.api.iso.CurrencyDataProvider;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.moneyfp.FpCurrencyStd;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpCurrencyStdAdapter {
    public static CurrencyDataProvider dataProvider = JavaCurrencyDataProvider.instance;

    /** Convert the custom type into a serializable BonaPortable. */
    public static FpCurrencyStd toBonaPortable(FPCurrency currency) {
        return new FpCurrencyStd(currency.getCurrencyCode());
    }
    
    /** Convert a parsed adapter type into the custom type. 
     * @throws E */
    public static <E extends Exception> FPCurrency fromBonaPortable(BonaPortable obj, MessageParser<E> p) throws E {
        if (obj instanceof FpCurrencyStd) {
            FpCurrencyStd currency = (FpCurrencyStd)obj;
            try {
                return new FPCurrency(dataProvider.get(currency.getCurrencyCode()));
            } catch (Exception e) {
                throw p.customExceptionConverter("FPCurrency(" + currency.getCurrencyCode() + ") not accepted", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }
}
