package de.jpaw.bonaparte.jpa.converters;

import java.util.Map;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.StaticMeta;
import de.jpaw.bonaparte.jpa.json.CompactJsonObject;

// persists a serialized object in a BLOB / bytea
@Converter(autoApply = true)
public class ConverterCompactJsonObject implements AttributeConverter<CompactJsonObject, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(CompactJsonObject obj) {
        return obj == null ? null : CompactByteArrayComposer.marshalAsJson(StaticMeta.OUTER_BONAPORTABLE_FOR_JSON, obj.getData(), false);
    }

    @Override
    public CompactJsonObject convertToEntityAttribute(byte[] data) {
        Map<String, Object> l = CompactByteArrayParser.unmarshalJson(data, StaticMeta.OUTER_BONAPORTABLE_FOR_JSON);
        return l == null ? null : new CompactJsonObject(l);
    }
}
