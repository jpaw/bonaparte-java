package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 7 fractional digits. */
public class ScaledLongAdapter7Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter7Exact() {
        super(7, false);
    }
}
