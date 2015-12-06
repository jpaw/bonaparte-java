package de.jpaw.bonaparte.api.codecs.impl;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;

public class CastIt {

    public static <O extends BonaPortable> O castTo(BonaPortable obj, Class<O> clazz) throws MessageParserException {
        if (clazz.isAssignableFrom(obj.getClass()))
            return (O)obj;
        throw new MessageParserException(MessageParserException.WRONG_CLASS);
    }
}
