package de.jpaw.bonaparte.adapters.moneyfp;

import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpSingleAmountExtAdapter {
    public static long marshal(FPAmount obj) {
        return obj.getGross();
    }
    
    public static <E extends Exception> FPAmount unmarshal(FPCurrency currency, Long gross, ExceptionConverter<E> p) throws E {
        return gross == null ? null : new FPAmount(currency, gross);
    }
}
