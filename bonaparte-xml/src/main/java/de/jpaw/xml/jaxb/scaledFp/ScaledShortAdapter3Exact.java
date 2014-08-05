package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 3 fractional digits. */
public class ScaledShortAdapter3Exact extends AbstractScaledShortAdapter {

    public ScaledShortAdapter3Exact() {
        super(3, false);
    }
}
