package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 11 fractional digits. */
public class ScaledLongAdapter11Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter11Exact() {
        super(11, false);
    }
}
