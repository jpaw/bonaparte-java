package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 8 fractional digits. */
public class ScaledIntegerAdapter8Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter8Exact() {
        super(8, false);
    }
}
