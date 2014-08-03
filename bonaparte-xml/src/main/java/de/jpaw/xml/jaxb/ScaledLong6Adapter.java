package de.jpaw.xml.jaxb;

/** XmlAdapter for fixed-point 6 decimals (microunits) without rounding. */
public class ScaledLong6Adapter extends AbstractScaledLongAdapter {

    public ScaledLong6Adapter(int scale, boolean allowRounding) {
        super(6, false);
    }
}
