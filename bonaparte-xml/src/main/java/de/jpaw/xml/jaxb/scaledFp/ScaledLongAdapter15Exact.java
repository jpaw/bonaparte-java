package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 15 fractional digits. */
public class ScaledLongAdapter15Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter15Exact() {
        super(15, false);
    }
}
