package de.jpaw.bonaparte.jpa;

import java.sql.Types;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.jpa.json.NativeJsonElement;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;

// convert between the Java type "NativeJsonElement(Object)" and a text, which in the database will be used as JSON
// a type cast is required on the database to avoid Postgres's type errors!
public class ElementConverter extends AbstractConverter implements Converter {

    private static final long serialVersionUID = 166787L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(ElementConverter.class);

    // parse String to Map (inside NativeJsonElement)
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        try {
            return dataValue == null ? null : new NativeJsonElement(new JsonParser((String) dataValue, false).parseElement());
        } catch (JsonException e) {
            LOGGER.error("Cannot parse JSON data: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // print Map in JSON format, also expand any BonaPortables included
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue == null ? null : BonaparteJsonEscaper.asJson(((NativeJsonElement) objectValue).getData());
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
        ((AbstractDirectMapping) mapping).setFieldType(Types.NVARCHAR);  // candidates are JAVA_OBJECT, OTHER, NVARCHAR etc...

        // field type setting adapted from http://stackoverflow.com/questions/13346089/using-uuid-with-eclipselink-and-postgresql
        final DatabaseField field = mapping.getField();
        if (field != null) {
            LOGGER.info("mapping.getField is not null");
            if (isPostgres(session)) {
                field.setColumnDefinition("jsonb");
//                field.setType(NativeJsonElement.class);
//                field.setTypeName("java.lang.Object");
            }
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
