package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledShortAdapter;

/** XmlAdapter for fixed-point arithmetic using 4 fractional digits. */
public class ScaledShortAdapter4Exact extends AbstractScaledShortAdapter {

    public ScaledShortAdapter4Exact() {
        super(4, false);
    }
}
