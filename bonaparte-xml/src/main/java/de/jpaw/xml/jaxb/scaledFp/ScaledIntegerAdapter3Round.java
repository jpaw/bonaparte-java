package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 3 fractional digits. */
public class ScaledIntegerAdapter3Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter3Round() {
        super(3, true);
    }
}
