package de.jpaw.xml.jaxb.scaledFp;

import de.jpaw.xml.jaxb.AbstractScaledLongAdapter;

/** XmlAdapter for fixed-point arithmetic using 9 fractional digits. */
public class ScaledLongAdapter9Exact extends AbstractScaledLongAdapter {

    public ScaledLongAdapter9Exact() {
        super(9, false);
    }
}
