package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 8 fractional digits. */
public class ScaledIntegerAdapter8Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter8Round() {
        super(8, true);
    }
}
