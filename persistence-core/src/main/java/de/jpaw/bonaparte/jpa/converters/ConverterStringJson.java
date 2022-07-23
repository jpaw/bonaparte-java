package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.json.JsonParser;
import jakarta.persistence.AttributeConverter;

// persists a serialized object in a CLOB / varchar(2)
// @Converter(autoApply = true)
public class ConverterStringJson implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> obj) {
        return obj == null ? null : BonaparteJsonEscaper.asJson(obj, false);
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String data) {
    	if (data == null || data.length() == 0)
    		return null;
        return new JsonParser(data, false).parseObject();
    }
}
