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
import de.jpaw.bonaparte.jpa.json.NativeJsonArray;
import de.jpaw.json.JsonException;
import de.jpaw.json.JsonParser;

// convert between the Java type "NativeJsonArray(List<Object>)" and a text, which in the database will be used as JSON
// a type cast is required on the database to avoid Postgres's type errors!

// see http://stackoverflow.com/questions/32238884/storing-json-jsonb-hstore-xml-enum-ipaddr-etc-fails-with-column-x-is-of
//CREATE OR REPLACE FUNCTION json_intext(text) RETURNS json AS $$
//SELECT json_in($1::cstring);
//$$ LANGUAGE SQL IMMUTABLE;
//
//CREATE CAST (text AS json)
//WITH FUNCTION json_intext(text) AS IMPLICIT;


public class ArrayConverter extends AbstractConverter implements Converter {

    private static final long serialVersionUID = 166783L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(ArrayConverter.class);

    // parse String to List (inside NativeJsonArray)
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        try {
            return dataValue == null ? null : new NativeJsonArray(new JsonParser((String) dataValue, false).parseArray());
        } catch (JsonException e) {
            LOGGER.error("Cannot parse JSON data: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // print List in JSON format, also expand any BonaPortables included
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue == null ? null : BonaparteJsonEscaper.asJson(((NativeJsonArray) objectValue).getData());
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
//                field.setType(NativeJsonObject.class);
//                field.setTypeName("java.util.Map");
            }
        }
    }

    @Override
    public boolean isMutable() {
        return true;
    }
}
