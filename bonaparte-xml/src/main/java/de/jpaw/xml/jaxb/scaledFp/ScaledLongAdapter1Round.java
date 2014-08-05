package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 1 fractional digits. */
public class ScaledLongAdapter1Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter1Round() {
        super(1, true);
    }
}
