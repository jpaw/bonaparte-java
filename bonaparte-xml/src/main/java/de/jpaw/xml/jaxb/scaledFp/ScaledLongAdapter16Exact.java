package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 16 fractional digits. */
public class ScaledLongAdapter16Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter16Exact() {
        super(16, false);
    }
}
