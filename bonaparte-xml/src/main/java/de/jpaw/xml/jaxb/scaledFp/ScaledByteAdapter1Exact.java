package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledByteAdapter;

/** XmlAdapter for fixed-point arithmetic using 1 fractional digits. */
public class ScaledByteAdapter1Exact extends AbstractScaledByteAdapter {

    public ScaledByteAdapter1Exact() {
        super(1, false);
    }
}
