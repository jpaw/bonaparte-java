package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 7 fractional digits. */
public class ScaledIntegerAdapter7Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter7Round() {
        super(7, true);
    }
}
