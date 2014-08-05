package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 4 fractional digits. */
public class ScaledShortAdapter4Round extends AbstractScaledShortAdapter {

    public ScaledShortAdapter4Round() {
        super(4, true);
    }
}
