package de.jpaw.bonaparte.util;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;

/** Implementing classes provide an easy way to convert between serialized formats and BonaPortables. */
public interface QuickConverter<S> {
    static final int INITIAL_BUFFER_SIZE = 1024;        // tunable constant

    S marshal(BonaPortable obj);
    <T extends BonaPortable> T unmarshal(S data, Class<T> expectedClass) throws MessageParserException;
}
