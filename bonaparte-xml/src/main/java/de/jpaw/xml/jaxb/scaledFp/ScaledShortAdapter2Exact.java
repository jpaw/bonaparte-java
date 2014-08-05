package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledShortAdapter2Exact extends AbstractScaledShortAdapter {

    public ScaledShortAdapter2Exact() {
        super(2, false);
    }
}
