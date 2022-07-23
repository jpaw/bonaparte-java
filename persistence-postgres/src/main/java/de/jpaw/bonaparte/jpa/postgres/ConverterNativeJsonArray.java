package de.jpaw.bonaparte.jpa.postgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.postgresql.util.PGobject;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.jpa.json.CompactJsonArray;
import de.jpaw.json.JsonParser;

// persists a serialized object in a jsonb column for Postgres
@Converter(autoApply = true)
public class ConverterNativeJsonArray implements AttributeConverter<CompactJsonArray, Object> {
	
    @Override
    public Object convertToDatabaseColumn(CompactJsonArray obj) {
        if (obj == null || obj.getData() == null)
        	return null;
        PGobject out = new PGobject();
        out.setType("jsonb");
        try {
			out.setValue(BonaparteJsonEscaper.asJson(obj.getData()));
		} catch (SQLException e) {
			throw new RuntimeException("Cannot set jsonb value", e);
		}
        return out;
    }

    @Override
    public CompactJsonArray convertToEntityAttribute(Object data) {
        if (data == null)
        	return null;
        if (!(data instanceof PGobject))
        	throw new RuntimeException("Did not get expected class PGObject, but " + data.getClass().getCanonicalName());
        PGobject obj = (PGobject)data;
        if (!("jsonb".equals(obj.getType())))
        	throw new RuntimeException("Did get PGObject, but not expected type jsonb, but " + obj.getType());
        final String str = obj.getValue(); 
        if (str == null || str.trim().length() == 0)
        	return null;

        List<Object> l = new JsonParser(str, true).parseArray();
        return l == null ? null : new CompactJsonArray(l);
    }
}
