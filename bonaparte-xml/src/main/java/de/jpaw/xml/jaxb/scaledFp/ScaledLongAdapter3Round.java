package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 3 fractional digits. */
public class ScaledLongAdapter3Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter3Round() {
        super(3, true);
    }
}
