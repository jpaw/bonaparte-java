package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 17 fractional digits. */
public class ScaledLongAdapter17Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter17Round() {
        super(17, true);
    }
}
