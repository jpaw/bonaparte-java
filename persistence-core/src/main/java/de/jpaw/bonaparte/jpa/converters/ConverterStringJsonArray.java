package de.jpaw.bonaparte.jpa.converters;

import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.jpa.json.StringJsonArray;
import de.jpaw.json.JsonParser;

// persists a serialized object in a CLOB / varchar(2)
@Converter(autoApply = true)
public class ConverterStringJsonArray implements AttributeConverter<StringJsonArray, String> {
    @Override
    public String convertToDatabaseColumn(StringJsonArray obj) {
        return obj == null ? null : BonaparteJsonEscaper.asJson(obj.getData());
    }

    @Override
    public StringJsonArray convertToEntityAttribute(String data) {
    	if (data == null || data.length() == 0)
    		return null;
        final List<Object> l = new JsonParser(data, true).parseArray();
        return l == null ? null : new StringJsonArray(l);
    }
}
