package de.jpaw.money;

import java.math.BigDecimal;
import java.util.List;

public interface MoneyGetter {
    public BigDecimal getGrossAmount();
    public BigDecimal getNetAmount();
    public List<BigDecimal> getTaxAmounts();
}
