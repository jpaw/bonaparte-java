package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 16 fractional digits. */
public class ScaledLongAdapter16Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter16Round() {
        super(16, true);
    }
}
