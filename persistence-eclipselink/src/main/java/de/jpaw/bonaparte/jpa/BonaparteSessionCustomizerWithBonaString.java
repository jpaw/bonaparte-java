package de.jpaw.bonaparte.jpa;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.util.impl.ConverterBonaString;

/** Enhanced session customizer, which adds support for BonaPortables, which are stored in text fields. */
public class BonaparteSessionCustomizerWithBonaString extends BonaparteSessionCustomizer {

    public BonaparteSessionCustomizerWithBonaString() {
        super();
        LOGGER.info("adding Bonaportable user types (as Strings)");
        convertersPerType.put(BonaPortable.class, new BonaPortableConverter<String>(new ConverterBonaString(), true));
    }
}
