package de.jpaw.money;

import java.math.BigDecimal;
import java.util.List;

public interface MoneySetter {
    public void setAmount(BigDecimal grossAmount);                  // gross or sum
    public void setComponentAmounts(List<BigDecimal> taxAmounts);   // net + taxes, or components
}
