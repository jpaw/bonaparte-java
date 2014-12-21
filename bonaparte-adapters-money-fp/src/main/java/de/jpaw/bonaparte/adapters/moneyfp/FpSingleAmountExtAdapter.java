package de.jpaw.bonaparte.adapters.moneyfp;

import de.jpaw.bonaparte.core.MessageComposer;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.moneyfp.FpSingleAmountExt;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpSingleAmountExtAdapter {
    public static <E extends Exception> void marshal(ObjectReference di, FPAmount obj, MessageComposer<E> w) throws E {
        w.addField(FpSingleAmountExt.meta$$gross, obj.getGross());
    }
    
    public static <E extends Exception> FPAmount unmarshal(FPCurrency currency, ObjectReference di, MessageParser<E> p) throws E {
        Long gross = p.readLong(FpSingleAmountExt.meta$$gross);
        return gross == null ? null : new FPAmount(currency, gross);
    }
}
