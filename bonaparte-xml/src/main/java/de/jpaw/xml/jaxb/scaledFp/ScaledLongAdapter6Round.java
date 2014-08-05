package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 6 fractional digits. */
public class ScaledLongAdapter6Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter6Round() {
        super(6, true);
    }
}
