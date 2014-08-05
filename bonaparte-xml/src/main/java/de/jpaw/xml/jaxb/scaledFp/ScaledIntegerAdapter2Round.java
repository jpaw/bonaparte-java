package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledIntegerAdapter;

/** XmlAdapter for fixed-point arithmetic using 2 fractional digits. */
public class ScaledIntegerAdapter2Round extends AbstractScaledIntegerAdapter {

    public ScaledIntegerAdapter2Round() {
        super(2, true);
    }
}
