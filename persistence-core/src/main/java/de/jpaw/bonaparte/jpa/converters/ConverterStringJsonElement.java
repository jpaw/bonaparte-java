package de.jpaw.bonaparte.jpa.converters;

import de.jpaw.bonaparte.core.BonaparteJsonEscaper;
import de.jpaw.bonaparte.jpa.json.StringJsonElement;
import de.jpaw.json.JsonParser;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// persists a serialized object in a CLOB / varchar(2)
@Converter(autoApply = true)
public class ConverterStringJsonElement implements AttributeConverter<StringJsonElement, String> {
    @Override
    public String convertToDatabaseColumn(StringJsonElement obj) {
        return obj == null ? null : BonaparteJsonEscaper.asJson(obj.getData());
    }

    @Override
    public StringJsonElement convertToEntityAttribute(String data) {
    	if (data == null || data.length() == 0)
    		return null;
        final Object l = new JsonParser(data, true).parseElement();
        return l == null ? null : new StringJsonElement(l);
    }
}
