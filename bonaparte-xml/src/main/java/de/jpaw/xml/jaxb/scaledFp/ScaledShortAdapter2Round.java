package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledShortAdapter2Round extends AbstractScaledShortAdapter {

    public ScaledShortAdapter2Round() {
        super(2, true);
    }
}
