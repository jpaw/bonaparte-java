package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 1 fractional digits. */
public class ScaledLongAdapter1Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter1Exact() {
        super(1, false);
    }
}
