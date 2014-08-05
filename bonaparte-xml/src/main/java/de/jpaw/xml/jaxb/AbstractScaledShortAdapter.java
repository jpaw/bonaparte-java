package de.jpaw.xml.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractScaledShortAdapter extends XmlAdapter<BigDecimal, Short> {
    private static final Short ZERO = Short.valueOf((short) 0);
    private final boolean allowRounding;
    private final int scale;
    
    protected AbstractScaledShortAdapter(int scale, boolean allowRounding) {
        this.scale = scale;
        this.allowRounding = allowRounding;
    }
 
    @Override
    public Short unmarshal(BigDecimal v) throws Exception {
        if (v.signum() == 0)
            return ZERO;  // always valid
        BigDecimal vv = v.setScale(scale, allowRounding ? RoundingMode.HALF_EVEN : RoundingMode.UNNECESSARY);
        return vv.unscaledValue().shortValue();
    }
 
    @Override
    public BigDecimal marshal(Short v) throws Exception {
        return BigDecimal.valueOf(v.shortValue(), scale);
    }
}
