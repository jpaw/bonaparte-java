package de.jpaw.bonaparte.adapters.moneyfp;

import java.util.List;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.moneyfp.FpAmountExt;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

public class FpAmountExtAdapter {
    /** Convert the custom type into a serializable BonaPortable. */
    public static FpAmountExt toBonaPortable(FPAmount amount) {
        List<Long> components = amount.getAmounts();
        return new FpAmountExt(amount.getGross(), components.size() == 0 ? null : components);
    }
    
    /** Convert a parsed adapter type into the custom type. */
    public static <E extends Exception> FPAmount fromBonaPortable(FPCurrency curr, BonaPortable obj, MessageParser<E> p) throws E {
        if (obj instanceof FpAmountExt) {
            FpAmountExt amount = (FpAmountExt)obj;
            try {
                return new FPAmount(amount.getAmounts(), amount.getGross(), curr);
            } catch (Exception e) {
                throw p.customExceptionConverter("FPAmount(" + obj + ") not accepted", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }

}
