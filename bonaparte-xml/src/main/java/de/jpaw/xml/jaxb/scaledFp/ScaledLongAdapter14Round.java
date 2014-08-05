package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 14 fractional digits. */
public class ScaledLongAdapter14Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter14Round() {
        super(14, true);
    }
}
