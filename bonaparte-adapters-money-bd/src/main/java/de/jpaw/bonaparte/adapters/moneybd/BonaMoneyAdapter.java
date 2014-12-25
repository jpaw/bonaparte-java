package de.jpaw.bonaparte.adapters.moneybd;

import java.math.BigDecimal;
import java.util.List;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ExceptionConverter;
import de.jpaw.bonaparte.pojos.adapters.moneybd.BAmount;
import de.jpaw.money.BonaCurrency;
import de.jpaw.money.BonaMoney;
import de.jpaw.money.MonetaryException;

public class BonaMoneyAdapter {
    static private final BigDecimal [] EMPTY_ARRAY = new BigDecimal [0];
    
    /** Convert the custom type into a serializable BonaPortable. */
    public static BAmount marshal(BonaMoney m) {
        List<BigDecimal> components = m.getComponentAmounts();
        return new BAmount(m.getCurrency(), m.getAmount(), components.size() == 0 ? null : components);
    }
    
    /** Convert a parsed adapter type into the custom type. */
    public static <E extends Exception> BonaMoney unmarshal(BonaPortable obj, ExceptionConverter<E> p) throws E {
        if (obj instanceof BAmount) {
            BAmount m = (BAmount)obj;
            try {
                BonaCurrency currency = m.getCurrency();
                if (m.getComponents() == null) {
                    return new BonaMoney(currency, BAmount.meta$$gross.getRounding(), m.getGross());
                } else {
                    BigDecimal [] componentArray = m.getComponents().toArray(EMPTY_ARRAY);
                    return new BonaMoney(currency, BAmount.meta$$gross.getRounding(), false, m.getGross(), componentArray);
                }
            } catch (MonetaryException e) {
                throw p.customExceptionConverter("Cannot convert " + obj + " to BonaMoney", e);
            }
        } else {
            throw new IllegalArgumentException("Incorrect type returned");
        }
    }

}
