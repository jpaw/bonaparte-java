package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledByteAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledByteAdapter2Round extends AbstractScaledByteAdapter {

    public ScaledByteAdapter2Round() {
        super(2, true);
    }
}
