package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 12 fractional digits. */
public class ScaledLongAdapter12Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter12Round() {
        super(12, true);
    }
}
