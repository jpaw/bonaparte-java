package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledLongAdapter2Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter2Exact() {
        super(2, false);
    }
}
