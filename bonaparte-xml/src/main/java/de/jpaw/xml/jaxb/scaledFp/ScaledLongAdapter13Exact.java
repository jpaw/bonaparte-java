package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 13 fractional digits. */
public class ScaledLongAdapter13Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter13Exact() {
        super(13, false);
    }
}
