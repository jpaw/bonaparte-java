package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledIntegerAdapter2Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter2Exact() {
        super(2, false);
    }
}
