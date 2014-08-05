package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 6 fractional digits. */
public class ScaledIntegerAdapter6Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter6Round() {
        super(6, true);
    }
}
