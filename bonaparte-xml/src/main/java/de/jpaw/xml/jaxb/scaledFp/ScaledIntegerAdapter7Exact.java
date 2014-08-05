package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 7 fractional digits. */
public class ScaledIntegerAdapter7Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter7Exact() {
        super(7, false);
    }
}
