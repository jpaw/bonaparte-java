package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.util.impl.ConverterBonaByte;

/** Enhanced session customizer, which adds support for BonaPortables, which are stored in binary fields. */
public class BonaparteSessionCustomizerWithBonaByte extends BonaparteSessionCustomizer {

    public BonaparteSessionCustomizerWithBonaByte() {
        super();
        LOGGER.info("adding Bonaportable user types (as byte [])");
        convertersPerType.put(BonaPortable.class, new BonaPortableConverter<byte []>(new ConverterBonaByte(), false));
    }
}
