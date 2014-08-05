package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 13 fractional digits. */
public class ScaledLongAdapter13Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter13Round() {
        super(13, true);
    }
}
