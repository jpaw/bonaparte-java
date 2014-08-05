package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 18 fractional digits. */
public class ScaledLongAdapter18Round extends AbstractScaledLongAdapter {

    public ScaledLongAdapter18Round() {
        super(18, true);
    }
}
