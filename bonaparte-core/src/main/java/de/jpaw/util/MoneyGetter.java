package de.jpaw.util;

import java.math.BigDecimal;
import java.util.List;

public interface MoneyGetter {
    public BigDecimal getAmount();                  // gross or sum
    public List<BigDecimal> getComponentAmounts();  // net + taxes, or line items
}
