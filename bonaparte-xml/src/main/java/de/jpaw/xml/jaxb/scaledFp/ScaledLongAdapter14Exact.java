package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 14 fractional digits. */
public class ScaledLongAdapter14Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter14Exact() {
        super(14, false);
    }
}
