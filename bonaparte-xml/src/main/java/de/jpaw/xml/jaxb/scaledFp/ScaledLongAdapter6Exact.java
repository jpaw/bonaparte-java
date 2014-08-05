package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 6 fractional digits. */
public class ScaledLongAdapter6Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter6Exact() {
        super(6, false);
    }
}
