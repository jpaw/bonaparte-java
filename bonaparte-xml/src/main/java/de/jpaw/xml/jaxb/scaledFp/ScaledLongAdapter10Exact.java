package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 10 fractional digits. */
public class ScaledLongAdapter10Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter10Exact() {
        super(10, false);
    }
}
