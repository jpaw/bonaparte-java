package de.jpaw.bonaparte.extensions;

import java.util.List;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageComposer;

/** Useful methods which work with most Composers, but are in fact static utility functions are therefore not part of the BonaPortable interface. */
public class ComposerExtensions {

    /** Serialize a multi-record message (encapsulated into a transmission) into a pre-allocated composer. */
    public static <E extends Exception> void transmission(MessageComposer<E> composer, List<BonaPortable> objects) throws E {
        composer.startTransmission();
        for (BonaPortable o : objects)
            composer.writeRecord(o);
        composer.terminateTransmission();
    }

}
