package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 6 fractional digits. */
public class ScaledIntegerAdapter6Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter6Exact() {
        super(6, false);
    }
}
