package de.jpaw.bonaparte.jpa;

import java.sql.Types;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.util.QuickConverter;

public class BonaPortableConverter<S> implements Converter {
    private static final long serialVersionUID = 12469283476L;

    private final QuickConverter<S> myConverter;
    private final boolean isText;

    BonaPortableConverter(QuickConverter<S> myConverter, boolean isText) {
        this.myConverter = myConverter;
        this.isText = isText;
    }

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        try {
            return myConverter.unmarshal((S)dataValue, BonaPortable.class);
        } catch (MessageParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return myConverter.marshal((BonaPortable)objectValue);
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        ((AbstractDirectMapping) mapping).setFieldType(isText ? Types.NCLOB : Types.BLOB);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

}
