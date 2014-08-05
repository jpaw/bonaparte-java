package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 4 fractional digits. */
public class ScaledIntegerAdapter4Exact extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter4Exact() {
        super(4, false);
    }
}
