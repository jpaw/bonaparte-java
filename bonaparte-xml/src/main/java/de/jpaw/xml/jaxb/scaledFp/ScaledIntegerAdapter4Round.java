package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 4 fractional digits. */
public class ScaledIntegerAdapter4Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter4Round() {
        super(4, true);
    }
}
