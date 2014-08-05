package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 12 fractional digits. */
public class ScaledLongAdapter12Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter12Exact() {
        super(12, false);
    }
}
