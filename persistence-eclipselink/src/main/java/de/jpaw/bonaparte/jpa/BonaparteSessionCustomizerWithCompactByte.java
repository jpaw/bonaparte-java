package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.util.impl.ConverterCompactByte;

/** Enhanced session customizer, which adds support for BonaPortables, which are stored in compact form in binary fields. */
public class BonaparteSessionCustomizerWithCompactByte extends BonaparteSessionCustomizer {

    public BonaparteSessionCustomizerWithCompactByte() {
        super();
        LOGGER.info("adding Bonaportable user types (as byte [])");
        convertersPerType.put(BonaPortable.class, new BonaPortableConverter<byte []>(new ConverterCompactByte(), false));
    }
}
