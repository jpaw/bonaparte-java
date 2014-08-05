package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 1 fractional digits. */
public class ScaledIntegerAdapter1Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter1Exact() {
        super(1, false);
    }
}
