package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.jpa.json.StringJsonObject;
import de.jpaw.json.JsonParser;

// persists a serialized object in a CLOB / varchar(2)
@Converter(autoApply = true)
public class ConverterStringJsonObject implements AttributeConverter<StringJsonObject, String> {
    @Override
    public String convertToDatabaseColumn(StringJsonObject obj) {
        return obj == null ? null : BonaparteJsonEscaper.asJson(obj.getData());
    }

    @Override
    public StringJsonObject convertToEntityAttribute(String data) {
    	if (data == null || data.length() == 0)
    		return null;
        Map<String, Object> l = new JsonParser(data, true).parseObject();
        return l == null ? null : new StringJsonObject(l);
    }
}
