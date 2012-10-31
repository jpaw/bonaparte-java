package de.jpaw.bonaparte.jpa;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;

public class BonaPortableConverter implements Converter {

    private static final long serialVersionUID = 1L;

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }
        ByteArrayParser parser = new ByteArrayParser((byte[]) dataValue, 0, -1);
        try {
            return parser.readObject(BonaPortable.class, true, true);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        } else {
            ByteArrayComposer composer = new ByteArrayComposer();
            composer.addField((BonaPortable) objectValue);
            return composer.getBytes();
        }
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }

    @Override
    public boolean isMutable() {
        return false;
    }

}