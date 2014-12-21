package de.jpaw.bonaparte.adapters.moneyfp;

import de.jpaw.api.iso.CurrencyDataProvider;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.moneyfp.FpCurrencyStd;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpCurrencyStdAdapter {
    public static CurrencyDataProvider dataProvider = JavaCurrencyDataProvider.instance;
    
    public static <E extends Exception> void marshal(ObjectReference di, FPCurrency obj, MessageComposer<E> w) throws E {
        w.addField(FpCurrencyStd.meta$$currencyCode, obj.getCurrencyCode());
    }
    
    public static <E extends Exception> FPCurrency unmarshal(ObjectReference di, MessageParser<E> p) throws E {
        String code = p.readAscii(FpCurrencyStd.meta$$currencyCode);
        return code == null ? null : FPCurrency.stdPrecisionOf(dataProvider.get(code));
    }
}
