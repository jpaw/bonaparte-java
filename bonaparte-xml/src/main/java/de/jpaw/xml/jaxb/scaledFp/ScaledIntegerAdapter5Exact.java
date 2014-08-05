package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 5 fractional digits. */
public class ScaledIntegerAdapter5Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter5Exact() {
        super(5, false);
    }
}
