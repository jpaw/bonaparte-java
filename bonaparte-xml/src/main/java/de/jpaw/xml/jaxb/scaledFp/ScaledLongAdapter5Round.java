package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 5 fractional digits. */
public class ScaledLongAdapter5Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter5Round() {
        super(5, true);
    }
}
