package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 4 fractional digits. */
public class ScaledLongAdapter4Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter4Round() {
        super(4, true);
    }
}
