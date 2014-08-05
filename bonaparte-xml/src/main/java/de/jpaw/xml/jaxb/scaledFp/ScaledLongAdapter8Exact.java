package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 8 fractional digits. */
public class ScaledLongAdapter8Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter8Exact() {
        super(8, false);
    }
}
