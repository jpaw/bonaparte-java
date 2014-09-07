package de.jpaw.adapters.fpmoney;

import java.util.List;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParser;
import de.jpaw.bonaparte.pojos.adapters.fpmoney.FpAmount;
import de.jpaw.fixedpoint.money.FPAmount;

public class FpAmountAdapter {
    /** Convert the custom type into a serializable BonaPortable. */
    public static FpAmount toBonaPortable(FPAmount amount) {
        List<Long> components = amount.getAmounts();
        return new FpAmount(amount.getCurrency(), amount.getGross(), components.size() == 0 ? null : components);
    }
    
    /** Convert a parsed adapter type into the custom type. */
    public static <E extends Exception> FPAmount fromBonaPortable(BonaPortable obj, MessageParser<E> p) throws E {
        if (obj instanceof FpAmount) {
            FpAmount amount = (FpAmount)obj;
            try {
                return new FPAmount(amount.getAmounts(), amount.getGross(), amount.getCurrency());
            } catch (Exception e) {
                throw p.customExceptionConverter("FPAmount(" + obj + ") not accepted", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }

}
