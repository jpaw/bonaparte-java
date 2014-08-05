package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 18 fractional digits. */
public class ScaledLongAdapter18Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter18Exact() {
        super(18, false);
    }
}
