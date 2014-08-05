package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 5 fractional digits. */
public class ScaledLongAdapter5Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter5Exact() {
        super(5, false);
    }
}
