package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 3 fractional digits. */
public class ScaledShortAdapter3Round extends AbstractScaledShortAdapter {

    public ScaledShortAdapter3Round() {
        super(3, true);
    }
}
