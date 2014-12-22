package de.jpaw.bonaparte.adapters.moneyfp;

import de.jpaw.api.iso.CurrencyDataProvider;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpCurrencyStdAdapter {
    public static CurrencyDataProvider dataProvider = JavaCurrencyDataProvider.instance;
    
    public static String marshal(FPCurrency obj) {
        return obj.getCurrencyCode();
    }
    
    public static <E extends Exception> FPCurrency unmarshal(String code, ExceptionConverter<E> p) throws E {
        return code == null ? null : FPCurrency.stdPrecisionOf(dataProvider.get(code));
    }
}
