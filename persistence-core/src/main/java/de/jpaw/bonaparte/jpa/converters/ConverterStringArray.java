package de.jpaw.bonaparte.jpa.converters;

import java.util.List;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.json.JsonParser;
import jakarta.persistence.AttributeConverter;

// persists a serialized object in a CLOB / varchar(2)
//@Converter(autoApply = true)
public class ConverterStringArray implements AttributeConverter<List, String> {
    @Override
    public String convertToDatabaseColumn(List obj) {
        return obj == null ? null : BonaparteJsonEscaper.asJson(obj, false);
    }

    @Override
    public List convertToEntityAttribute(String data) {
    	if (data == null || data.length() == 0)
    		return null;
        return new JsonParser(data, false).parseArray();
    }
}
