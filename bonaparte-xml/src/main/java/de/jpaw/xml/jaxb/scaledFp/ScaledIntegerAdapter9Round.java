package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 9 fractional digits. */
public class ScaledIntegerAdapter9Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter9Round() {
        super(9, true);
    }
}
