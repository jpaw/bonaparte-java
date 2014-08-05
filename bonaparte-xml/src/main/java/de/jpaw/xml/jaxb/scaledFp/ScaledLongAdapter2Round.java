package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledLongAdapter2Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter2Round() {
        super(2, true);
    }
}
