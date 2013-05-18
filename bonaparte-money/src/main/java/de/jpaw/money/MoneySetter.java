package de.jpaw.money;

import java.math.BigDecimal;
import java.util.List;

public interface MoneySetter {
    public void setGrossAmount(BigDecimal grossAmount);
    public void setNetAmount(BigDecimal netAmount);
    public void setTaxAmounts(List<BigDecimal> taxAmounts);
}
