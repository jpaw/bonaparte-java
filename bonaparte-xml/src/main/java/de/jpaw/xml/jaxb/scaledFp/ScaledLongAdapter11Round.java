package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 11 fractional digits. */
public class ScaledLongAdapter11Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter11Round() {
        super(11, true);
    }
}
